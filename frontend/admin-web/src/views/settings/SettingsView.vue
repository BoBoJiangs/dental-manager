<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="基础设置" description="维护组织、门诊、科室、诊室、员工与角色配置。">
          <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" @click="saveProfile">保存组织信息</el-button>
        </PageHeader>
      </div>
    </div>

    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="组织信息" name="profile">
            <el-form :model="profileForm" label-position="top" class="form-grid">
              <el-form-item label="组织编码">
                <el-input :model-value="orgProfile?.orgCode || '-'" disabled />
              </el-form-item>
              <el-form-item label="组织名称">
                <el-input v-model.trim="profileForm.orgName" :disabled="!hasButtonPermission('ORG_EDIT')" />
              </el-form-item>
              <el-form-item label="联系人">
                <el-input v-model.trim="profileForm.contactName" :disabled="!hasButtonPermission('ORG_EDIT')" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model.trim="profileForm.contactPhone" :disabled="!hasButtonPermission('ORG_EDIT')" />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="profileForm.status" :disabled="!hasButtonPermission('ORG_EDIT')">
                  <el-option label="启用" :value="1" />
                  <el-option label="停用" :value="0" />
                </el-select>
              </el-form-item>
              <el-form-item class="full-span" label="备注">
                <el-input v-model.trim="profileForm.remark" type="textarea" :rows="3" :disabled="!hasButtonPermission('ORG_EDIT')" />
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="门诊" name="clinics">
            <div class="toolbar" style="margin-bottom: 16px;">
              <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" @click="openClinicDialog()">新增门诊</el-button>
            </div>
            <el-table :data="clinics" size="large">
              <el-table-column prop="clinicCode" label="门诊编码" min-width="140" />
              <el-table-column prop="clinicName" label="门诊名称" min-width="160" />
              <el-table-column prop="clinicType" label="类型" min-width="120" />
              <el-table-column prop="address" label="地址" min-width="220" />
              <el-table-column prop="phone" label="电话" min-width="140" />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.status === 1 ? 'NORMAL' : 'FAIL'" />
                </template>
              </el-table-column>
              <el-table-column v-if="hasButtonPermission('ORG_EDIT')" fixed="right" label="操作" width="120">
                <template #default="{ row }">
                  <el-button link type="primary" @click="openClinicDialog(row)">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="科室/诊室" name="departments">
            <div class="split-grid">
              <div>
                <div class="page-header" style="margin-bottom: 12px;">
                  <div>
                    <h3 class="page-title" style="font-size: 18px;">科室</h3>
                  </div>
                  <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" link @click="openDepartmentDialog()">新增</el-button>
                </div>
                <el-table :data="departments" size="large">
                  <el-table-column prop="deptCode" label="科室编码" min-width="130" />
                  <el-table-column prop="deptName" label="科室" min-width="140" />
                  <el-table-column prop="clinicId" label="门诊 ID" min-width="100" />
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <StatusTag :status="row.status === 1 ? 'NORMAL' : 'FAIL'" />
                    </template>
                  </el-table-column>
                  <el-table-column v-if="hasButtonPermission('ORG_EDIT')" fixed="right" label="操作" width="100">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="openDepartmentDialog(row)">编辑</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <div>
                <div class="page-header" style="margin-bottom: 12px;">
                  <div>
                    <h3 class="page-title" style="font-size: 18px;">诊室</h3>
                  </div>
                  <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" link @click="openRoomDialog()">新增</el-button>
                </div>
                <el-table :data="rooms" size="large">
                  <el-table-column prop="roomCode" label="诊室编码" min-width="130" />
                  <el-table-column prop="roomName" label="诊室" min-width="140" />
                  <el-table-column prop="roomType" label="类型" min-width="120" />
                  <el-table-column prop="clinicId" label="门诊 ID" min-width="100" />
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <StatusTag :status="row.status === 1 ? 'NORMAL' : 'FAIL'" />
                    </template>
                  </el-table-column>
                  <el-table-column v-if="hasButtonPermission('ORG_EDIT')" fixed="right" label="操作" width="100">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="openRoomDialog(row)">编辑</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="员工" name="staff">
            <div class="toolbar" style="margin-bottom: 16px;">
              <el-input v-model.trim="staffFilters.keyword" placeholder="姓名/编码/手机号" clearable style="width: 260px;" />
              <el-select v-model="staffFilters.clinicId" clearable style="width: 160px;">
                <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
              </el-select>
              <el-select v-model="staffFilters.staffType" clearable style="width: 160px;">
                <el-option label="医生" value="DOCTOR" />
                <el-option label="前台" value="FRONT_DESK" />
                <el-option label="护士" value="NURSE" />
                <el-option label="财务" value="FINANCE" />
                <el-option label="管理" value="ADMIN" />
              </el-select>
              <el-button type="primary" @click="loadStaff">查询</el-button>
              <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" plain @click="openStaffDialog()">新增员工</el-button>
            </div>
            <el-table :data="staff" size="large">
              <el-table-column prop="staffCode" label="员工编码" min-width="130" />
              <el-table-column prop="staffName" label="姓名" min-width="120" />
              <el-table-column prop="staffType" label="岗位" min-width="120" />
              <el-table-column prop="jobTitle" label="职称" min-width="140" />
              <el-table-column prop="mobile" label="手机号" min-width="140" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <StatusTag :status="row.status === 1 ? 'NORMAL' : 'FAIL'" />
                </template>
              </el-table-column>
              <el-table-column v-if="hasButtonPermission('ORG_EDIT')" fixed="right" label="操作" width="100">
                <template #default="{ row }">
                  <el-button link type="primary" @click="openStaffDialog(row)">编辑</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="角色" name="roles">
            <div class="split-grid">
              <div>
                <div class="toolbar" style="margin-bottom: 16px;">
                  <el-button v-if="hasButtonPermission('ORG_EDIT')" type="primary" @click="openRoleDialog()">新增角色</el-button>
                </div>
                <el-table :data="roles" size="large" highlight-current-row @current-change="handleRoleSelect">
                <el-table-column prop="roleName" label="角色名称" min-width="160" />
                <el-table-column prop="roleCode" label="角色编码" min-width="160" />
                <el-table-column prop="dataScope" label="数据范围" min-width="120" />
                <el-table-column prop="status" label="状态" min-width="100" />
                <el-table-column prop="remark" label="备注" min-width="220" />
                <el-table-column v-if="hasButtonPermission('ORG_EDIT')" fixed="right" label="操作" width="100">
                  <template #default="{ row }">
                    <el-button link type="primary" @click.stop="openRoleDialog(row)">编辑</el-button>
                  </template>
                </el-table-column>
                </el-table>
              </div>
              <div class="page-card" style="box-shadow: none;">
                <div class="el-card__body">
                  <div class="page-header">
                    <div>
                      <h3 class="page-title" style="font-size: 18px;">权限矩阵</h3>
                      <p class="page-desc">{{ selectedRole?.roleName || '选择左侧角色查看菜单和按钮权限' }}</p>
                    </div>
                  </div>
                  <template v-if="selectedRole">
                    <el-descriptions :column="1" border style="margin-top: 12px;">
                      <el-descriptions-item label="角色编码">{{ selectedRole.roleCode }}</el-descriptions-item>
                      <el-descriptions-item label="数据范围">{{ selectedRole.dataScope }}</el-descriptions-item>
                      <el-descriptions-item label="菜单权限">
                        <div class="toolbar">
                          <el-tag v-for="item in selectedRole.menuPermissions || []" :key="item">{{ item }}</el-tag>
                        </div>
                      </el-descriptions-item>
                      <el-descriptions-item label="按钮权限">
                        <div class="toolbar">
                          <el-tag v-for="item in selectedRole.buttonPermissions || []" :key="item" type="success">{{ item }}</el-tag>
                        </div>
                      </el-descriptions-item>
                    </el-descriptions>
                  </template>
                  <div v-else class="empty-block" style="margin-top: 16px;">
                    <div>请选择一个角色查看权限矩阵</div>
                  </div>
                  <el-divider />
                  <div class="page-header">
                    <div>
                      <h3 class="page-title" style="font-size: 18px;">当前账号权限</h3>
                    </div>
                  </div>
                  <el-descriptions :column="1" border style="margin-top: 12px;" v-if="permissionMeta">
                    <el-descriptions-item label="当前角色">{{ (permissionMeta.roles || []).join(' / ') || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="当前数据范围">{{ (permissionMeta.dataScopes || []).join(' / ') || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="菜单权限">
                      <div class="toolbar">
                        <el-tag v-for="item in permissionMeta.menuPermissions || []" :key="item">{{ item }}</el-tag>
                      </div>
                    </el-descriptions-item>
                    <el-descriptions-item label="按钮权限">
                      <div class="toolbar">
                        <el-tag v-for="item in permissionMeta.buttonPermissions || []" :key="item" type="success">{{ item }}</el-tag>
                      </div>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="clinicDialogVisible" :title="clinicForm.id ? '编辑门诊' : '新增门诊'" width="720px">
      <el-form :model="clinicForm" label-position="top" class="form-grid">
        <el-form-item label="门诊编码"><el-input v-model.trim="clinicForm.clinicCode" /></el-form-item>
        <el-form-item label="门诊名称"><el-input v-model.trim="clinicForm.clinicName" /></el-form-item>
        <el-form-item label="门诊类型"><el-input v-model.trim="clinicForm.clinicType" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model.trim="clinicForm.phone" /></el-form-item>
        <el-form-item label="省份"><el-input v-model.trim="clinicForm.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model.trim="clinicForm.city" /></el-form-item>
        <el-form-item label="区域"><el-input v-model.trim="clinicForm.district" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="clinicForm.status">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="地址"><el-input v-model.trim="clinicForm.address" /></el-form-item>
        <el-form-item class="full-span" label="营业时间"><el-input v-model.trim="clinicForm.businessHours" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="clinicDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClinic">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="departmentDialogVisible" :title="departmentForm.id ? '编辑科室' : '新增科室'" width="680px">
      <el-form :model="departmentForm" label-position="top" class="form-grid">
        <el-form-item label="所属门诊">
          <el-select v-model="departmentForm.clinicId">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室编码"><el-input v-model.trim="departmentForm.deptCode" /></el-form-item>
        <el-form-item label="科室名称"><el-input v-model.trim="departmentForm.deptName" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="departmentForm.sortNo" :min="0" style="width: 100%;" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="departmentForm.status">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="备注"><el-input v-model.trim="departmentForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="departmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDepartment">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑诊室' : '新增诊室'" width="680px">
      <el-form :model="roomForm" label-position="top" class="form-grid">
        <el-form-item label="所属门诊">
          <el-select v-model="roomForm.clinicId">
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="诊室编码"><el-input v-model.trim="roomForm.roomCode" /></el-form-item>
        <el-form-item label="诊室名称"><el-input v-model.trim="roomForm.roomName" /></el-form-item>
        <el-form-item label="诊室类型"><el-input v-model.trim="roomForm.roomType" /></el-form-item>
        <el-form-item label="楼层"><el-input v-model.trim="roomForm.floorNo" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="roomForm.status">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoom">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="staffDialogVisible" :title="staffForm.id ? '编辑员工' : '新增员工'" width="760px">
      <el-form :model="staffForm" label-position="top" class="form-grid">
        <el-form-item label="员工编码"><el-input v-model.trim="staffForm.staffCode" /></el-form-item>
        <el-form-item label="员工姓名"><el-input v-model.trim="staffForm.staffName" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="staffForm.gender" clearable>
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model.trim="staffForm.mobile" /></el-form-item>
        <el-form-item label="证件号"><el-input v-model.trim="staffForm.idNo" /></el-form-item>
        <el-form-item label="职称"><el-input v-model.trim="staffForm.jobTitle" /></el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="staffForm.staffType">
            <el-option label="医生" value="DOCTOR" />
            <el-option label="前台" value="FRONT_DESK" />
            <el-option label="护士" value="NURSE" />
            <el-option label="财务" value="FINANCE" />
            <el-option label="管理" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="主门诊">
          <el-select v-model="staffForm.mainClinicId" clearable>
            <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室">
          <el-select v-model="staffForm.deptId" clearable>
            <el-option v-for="item in filteredDepartments" :key="item.id" :label="item.deptName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="在职状态">
          <el-select v-model="staffForm.status">
            <el-option label="在职" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期"><el-date-picker v-model="staffForm.entryDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item label="离职日期"><el-date-picker v-model="staffForm.leaveDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item label="医生标记">
          <el-select v-model="staffForm.isDoctor">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="擅长"><el-input v-model.trim="staffForm.specialty" /></el-form-item>
        <el-form-item class="full-span" label="备注"><el-input v-model.trim="staffForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="staffDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStaff">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" :title="roleForm.id ? '编辑角色' : '新增角色'" width="640px">
      <el-form :model="roleForm" label-position="top">
        <el-form-item label="角色编码">
          <el-input v-model.trim="roleForm.roleCode" :disabled="Boolean(roleForm.id)" />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model.trim="roleForm.roleName" />
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="roleForm.dataScope">
            <el-option label="全部数据" value="ALL" />
            <el-option label="门诊数据" value="CLINIC" />
            <el-option label="本人患者" value="SELF_PATIENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="roleForm.status">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model.trim="roleForm.remark" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox-group v-model="roleForm.menuPermissions">
            <el-checkbox v-for="item in availableMenuPermissions" :key="item" :value="item">{{ item }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="按钮权限">
          <el-checkbox-group v-model="roleForm.buttonPermissions">
            <el-checkbox v-for="item in availableButtonPermissions" :key="item" :value="item">{{ item }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <div class="page-desc">内置权限由角色编码匹配权限矩阵；自定义编码默认没有内置菜单/按钮权限。</div>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '@/api/service'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { availableButtonPermissions, availableMenuPermissions } from '@/config/permissions'
import { usePermission } from '@/composables/usePermission'

const { hasButtonPermission } = usePermission()
const activeTab = ref('profile')

const orgProfile = ref<any>(null)
const clinics = ref<any[]>([])
const departments = ref<any[]>([])
const rooms = ref<any[]>([])
const staff = ref<any[]>([])
const roles = ref<any[]>([])
const selectedRole = ref<any>(null)
const permissionMeta = ref<any>(null)
const roleDialogVisible = ref(false)

const staffFilters = reactive({
  clinicId: undefined as number | undefined,
  staffType: '',
  keyword: '',
})

const profileForm = reactive({
  orgName: '',
  contactName: '',
  contactPhone: '',
  status: 1,
  remark: '',
})

const clinicDialogVisible = ref(false)
const clinicForm = reactive<Record<string, any>>({
  id: undefined,
  clinicCode: '',
  clinicName: '',
  clinicType: '',
  province: '',
  city: '',
  district: '',
  address: '',
  phone: '',
  businessHours: '',
  status: 1,
})

const departmentDialogVisible = ref(false)
const departmentForm = reactive<Record<string, any>>({
  id: undefined,
  clinicId: undefined,
  deptCode: '',
  deptName: '',
  sortNo: 0,
  status: 1,
  remark: '',
})

const roomDialogVisible = ref(false)
const roomForm = reactive<Record<string, any>>({
  id: undefined,
  clinicId: undefined,
  roomCode: '',
  roomName: '',
  roomType: '',
  floorNo: '',
  status: 1,
})

const staffDialogVisible = ref(false)
const staffForm = reactive<Record<string, any>>({
  id: undefined,
  staffCode: '',
  staffName: '',
  gender: undefined,
  mobile: '',
  idNo: '',
  jobTitle: '',
  staffType: 'DOCTOR',
  mainClinicId: undefined,
  deptId: undefined,
  status: 1,
  entryDate: '',
  leaveDate: '',
  isDoctor: 1,
  specialty: '',
  remark: '',
})
const roleForm = reactive<Record<string, any>>({
  id: undefined,
  roleCode: '',
  roleName: '',
  dataScope: 'CLINIC',
  status: 1,
  remark: '',
  menuPermissions: [] as string[],
  buttonPermissions: [] as string[],
})

const filteredDepartments = computed(() =>
  departments.value.filter((item) => !staffForm.mainClinicId || item.clinicId === staffForm.mainClinicId),
)

async function loadSettings() {
  const [profileRes, clinicRes, deptRes, roomRes, roleRes, permissionRes] = await Promise.all([
    api.org.profile(),
    api.org.clinics(),
    api.org.departments(),
    api.org.rooms(),
    api.org.roles(),
    api.auth.permissions(),
  ])
  orgProfile.value = profileRes.data
  Object.assign(profileForm, {
    orgName: profileRes.data?.orgName || '',
    contactName: profileRes.data?.contactName || '',
    contactPhone: profileRes.data?.contactPhone || '',
    status: profileRes.data?.status ?? 1,
    remark: profileRes.data?.remark || '',
  })
  clinics.value = clinicRes.data || []
  departments.value = deptRes.data || []
  rooms.value = roomRes.data || []
  roles.value = roleRes.data || []
  selectedRole.value = roles.value[0] || null
  permissionMeta.value = permissionRes.data || null
}

async function loadStaff() {
  const response = await api.org.staff({
    clinicId: staffFilters.clinicId,
    staffType: staffFilters.staffType || undefined,
    keyword: staffFilters.keyword || undefined,
  })
  staff.value = response.data || []
}

async function saveProfile() {
  await api.org.updateProfile(profileForm)
  ElMessage.success('组织信息已保存')
  await loadSettings()
}

function openClinicDialog(row?: any) {
  Object.assign(clinicForm, {
    id: row?.id,
    clinicCode: row?.clinicCode || '',
    clinicName: row?.clinicName || '',
    clinicType: row?.clinicType || '',
    province: row?.province || '',
    city: row?.city || '',
    district: row?.district || '',
    address: row?.address || '',
    phone: row?.phone || '',
    businessHours: row?.businessHours || '',
    status: row?.status ?? 1,
  })
  clinicDialogVisible.value = true
}

async function submitClinic() {
  if (clinicForm.id) {
    await api.org.updateClinic(clinicForm.id, clinicForm)
    ElMessage.success('门诊已更新')
  } else {
    await api.org.createClinic(clinicForm)
    ElMessage.success('门诊已新增')
  }
  clinicDialogVisible.value = false
  await loadSettings()
}

function openDepartmentDialog(row?: any) {
  Object.assign(departmentForm, {
    id: row?.id,
    clinicId: row?.clinicId ?? clinics.value[0]?.id,
    deptCode: row?.deptCode || '',
    deptName: row?.deptName || '',
    sortNo: row?.sortNo ?? 0,
    status: row?.status ?? 1,
    remark: row?.remark || '',
  })
  departmentDialogVisible.value = true
}

async function submitDepartment() {
  if (departmentForm.id) {
    await api.org.updateDepartment(departmentForm.id, departmentForm)
    ElMessage.success('科室已更新')
  } else {
    await api.org.createDepartment(departmentForm)
    ElMessage.success('科室已新增')
  }
  departmentDialogVisible.value = false
  await loadSettings()
}

function openRoomDialog(row?: any) {
  Object.assign(roomForm, {
    id: row?.id,
    clinicId: row?.clinicId ?? clinics.value[0]?.id,
    roomCode: row?.roomCode || '',
    roomName: row?.roomName || '',
    roomType: row?.roomType || '',
    floorNo: row?.floorNo || '',
    status: row?.status ?? 1,
  })
  roomDialogVisible.value = true
}

async function submitRoom() {
  if (roomForm.id) {
    await api.org.updateRoom(roomForm.id, roomForm)
    ElMessage.success('诊室已更新')
  } else {
    await api.org.createRoom(roomForm)
    ElMessage.success('诊室已新增')
  }
  roomDialogVisible.value = false
  await loadSettings()
}

function openStaffDialog(row?: any) {
  Object.assign(staffForm, {
    id: row?.id,
    staffCode: row?.staffCode || '',
    staffName: row?.staffName || '',
    gender: row?.gender,
    mobile: row?.mobile || '',
    idNo: row?.idNo || '',
    jobTitle: row?.jobTitle || '',
    staffType: row?.staffType || 'DOCTOR',
    mainClinicId: row?.mainClinicId,
    deptId: row?.deptId,
    status: row?.status ?? 1,
    entryDate: row?.entryDate || '',
    leaveDate: row?.leaveDate || '',
    isDoctor: row?.isDoctor ?? (row?.staffType === 'DOCTOR' ? 1 : 0),
    specialty: row?.specialty || '',
    remark: row?.remark || '',
  })
  staffDialogVisible.value = true
}

async function submitStaff() {
  if (staffForm.id) {
    await api.org.updateStaff(staffForm.id, staffForm)
    ElMessage.success('员工已更新')
  } else {
    await api.org.createStaff(staffForm)
    ElMessage.success('员工已新增')
  }
  staffDialogVisible.value = false
  await loadStaff()
}

function handleRoleSelect(row: any) {
  selectedRole.value = row || null
}

function openRoleDialog(row?: any) {
  Object.assign(roleForm, {
    id: row?.id,
    roleCode: row?.roleCode || '',
    roleName: row?.roleName || '',
    dataScope: row?.dataScope || 'CLINIC',
    status: row?.status ?? 1,
    remark: row?.remark || '',
    menuPermissions: [...(row?.menuPermissions || [])],
    buttonPermissions: [...(row?.buttonPermissions || [])],
  })
  roleDialogVisible.value = true
}

async function submitRole() {
  if (roleForm.id) {
    await api.org.updateRole(roleForm.id, roleForm)
    ElMessage.success('角色已更新')
  } else {
    await api.org.createRole(roleForm)
    ElMessage.success('角色已新增')
  }
  roleDialogVisible.value = false
  await loadSettings()
}

onMounted(async () => {
  await loadSettings()
  await loadStaff()
})
</script>
