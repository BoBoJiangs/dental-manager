param(
    [switch]$SkipDocker,
    [switch]$SkipBackendCompile,
    [switch]$SkipFrontendInstall
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$frontendDir = Join-Path $repoRoot "frontend\admin-web"
$backendLog = Join-Path $repoRoot "target\smoke-backend.log"
$backendErrLog = Join-Path $repoRoot "target\smoke-backend.err.log"
$frontendLog = Join-Path $repoRoot "target\smoke-frontend.log"
$frontendErrLog = Join-Path $repoRoot "target\smoke-frontend.err.log"

$backendProcess = $null
$frontendProcess = $null

function Invoke-Step {
    param(
        [string]$Message,
        [scriptblock]$Action
    )
    Write-Host "==> $Message"
    & $Action
}

function Require-Command {
    param(
        [string]$Name,
        [string]$Hint
    )
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        if ($Hint) {
            throw "$Name command was not found. $Hint"
        }
        throw "$Name command was not found in PATH."
    }
}

function Test-PortInUse {
    param([int]$Port)
    $listener = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    return $null -ne $listener
}

function Wait-HttpReady {
    param(
        [string]$Url,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $Url -Method Get -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return
            }
        } catch {
            Start-Sleep -Seconds 2
            continue
        }
        Start-Sleep -Seconds 1
    }
    throw "Timed out waiting for URL: $Url"
}

function Stop-ProcessTree {
    param([System.Diagnostics.Process]$Process)

    if (-not $Process) {
        return
    }
    cmd /c "taskkill /PID $($Process.Id) /T /F >nul 2>&1"
}

try {
    Set-Location $repoRoot
    New-Item -ItemType Directory -Path (Join-Path $repoRoot "target") -Force | Out-Null

    Require-Command -Name "mvn" -Hint "Please install Maven and ensure mvn is available in PATH."
    Require-Command -Name "cmd.exe" -Hint ""

    if (Test-PortInUse -Port 8080) {
        throw "Port 8080 is already in use. Stop the existing process and rerun."
    }
    if (Test-PortInUse -Port 5173) {
        throw "Port 5173 is already in use. Stop the existing process and rerun."
    }

    if (-not $SkipDocker) {
        Require-Command -Name "docker" -Hint "Install Docker Desktop or rerun with -SkipDocker."
        Invoke-Step -Message "Starting local dependencies with docker compose" -Action {
            & docker compose up -d
            if ($LASTEXITCODE -ne 0) {
                throw "docker compose up -d failed."
            }
        }
    }

    if (-not $SkipBackendCompile) {
        Invoke-Step -Message "Compiling backend (skip tests)" -Action {
            & mvn -q -DskipTests compile
            if ($LASTEXITCODE -ne 0) {
                throw "mvn compile failed."
            }
        }
    }

    if ((-not $SkipFrontendInstall) -and (-not (Test-Path (Join-Path $frontendDir "node_modules")))) {
        Invoke-Step -Message "Installing frontend dependencies (npm ci)" -Action {
            Push-Location $frontendDir
            try {
                cmd /c npm ci
                if ($LASTEXITCODE -ne 0) {
                    throw "npm ci failed."
                }
            } finally {
                Pop-Location
            }
        }
    }

    Invoke-Step -Message "Starting backend service" -Action {
        $script:backendProcess = Start-Process `
            -FilePath "mvn" `
            -ArgumentList "-DskipTests", "spring-boot:run" `
            -WorkingDirectory $repoRoot `
            -RedirectStandardOutput $backendLog `
            -RedirectStandardError $backendErrLog `
            -PassThru
    }

    Invoke-Step -Message "Waiting for backend health endpoint" -Action {
        Wait-HttpReady -Url "http://127.0.0.1:8080/api/auth/health" -TimeoutSeconds 240
    }

    Invoke-Step -Message "Starting frontend dev server" -Action {
        $script:frontendProcess = Start-Process `
            -FilePath "cmd.exe" `
            -ArgumentList "/c", "npm run dev -- --host 127.0.0.1 --port 5173" `
            -WorkingDirectory $frontendDir `
            -RedirectStandardOutput $frontendLog `
            -RedirectStandardError $frontendErrLog `
            -PassThru
    }

    Invoke-Step -Message "Waiting for frontend login page" -Action {
        Wait-HttpReady -Url "http://127.0.0.1:5173/login" -TimeoutSeconds 180
    }

    Invoke-Step -Message "Running Playwright smoke tests" -Action {
        Push-Location $frontendDir
        try {
            cmd /c npm run test:e2e
            if ($LASTEXITCODE -ne 0) {
                throw "Playwright smoke tests failed."
            }
        } finally {
            Pop-Location
        }
    }

    Write-Host "Smoke checks completed successfully."
} catch {
    Write-Error $_
    Write-Host "Backend log:  $backendLog"
    Write-Host "Backend err:  $backendErrLog"
    Write-Host "Frontend log: $frontendLog"
    Write-Host "Frontend err: $frontendErrLog"
    throw
} finally {
    Stop-ProcessTree -Process $frontendProcess
    Stop-ProcessTree -Process $backendProcess
}
