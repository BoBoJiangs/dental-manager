<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <PageHeader title="门诊接诊" description="汇总病历、治疗计划、知情同意书、电子签名与牙位图数据。">
          <el-button v-if="hasButtonPermission('EMR_EDIT')" type="primary" @click="openRecordDialog()">新建病历</el-button>
          <el-button v-if="hasButtonPermission('EMR_EDIT')" type="primary" plain @click="openPlanDialog">新建治疗计划</el-button>
        </PageHeader>
      </div>
    </div>

    <div class="page-card tab-shell">
      <div class="el-card__body">
        <el-form :inline="true" :model="filters" class="toolbar" style="margin-bottom: 18px;">
          <el-form-item label="门诊">
            <el-select v-model="filters.clinicId" clearable style="width: 160px;">
              <el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="医生">
            <el-select v-model="filters.doctorId" clearable style="width: 160px;">
              <el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model.trim="filters.keyword" placeholder="病历号/患者/计划名称" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="病历记录" name="records">
            <el-table :data="records" v-loading="loading" size="large">
              <el-table-column prop="recordNo" label="病历号" min-width="120" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="doctorName" label="医生" min-width="120" />
              <el-table-column prop="visitDate" label="就诊时间" min-width="160" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.recordStatus" />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="100">
                <template #default="{ row }">
                  <el-space>
                    <el-button link type="primary" @click="openRecordDetail(row.id)">详情</el-button>
                    <el-button v-if="hasButtonPermission('EMR_EDIT')" link type="success" @click="openRecordDialog(row.id)">编辑</el-button>
                  </el-space>
                </template>
              </el-table-column>
            </el-table>
            <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
              <el-pagination
                v-model:current-page="recordPagination.current"
                v-model:page-size="recordPagination.size"
                background
                layout="total, prev, pager, next"
                :total="recordPagination.total"
                @current-change="handleRecordPageChange"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="治疗计划" name="plans">
            <el-table :data="plans" v-loading="loading" size="large">
              <el-table-column prop="planNo" label="计划号" min-width="120" />
              <el-table-column prop="planName" label="计划名称" min-width="180" />
              <el-table-column prop="patientName" label="患者" min-width="120" />
              <el-table-column prop="doctorName" label="医生" min-width="120" />
              <el-table-column prop="totalAmount" label="金额" min-width="120" />
              <el-table-column label="状态" width="120">
                <template #default="{ row }">
                  <StatusTag :status="row.planStatus" />
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="操作" width="200">
                <template #default="{ row }">
                  <el-space>
                    <el-button link type="primary" @click="openPlanDetail(row.id)">详情</el-button>
                    <el-dropdown v-if="hasButtonPermission('EMR_EDIT')" @command="(status: string | number | object) => updatePlanStatus(row.id, String(status))">
                      <el-button link type="success">状态</el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="DRAFT">草稿</el-dropdown-item>
                          <el-dropdown-item command="CONFIRMED">已确认</el-dropdown-item>
                          <el-dropdown-item command="IN_PROGRESS">进行中</el-dropdown-item>
                          <el-dropdown-item command="COMPLETED">已完成</el-dropdown-item>
                          <el-dropdown-item command="CANCELLED">已取消</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </el-space>
                </template>
              </el-table-column>
            </el-table>
            <div style="display: flex; justify-content: flex-end; margin-top: 16px;">
              <el-pagination
                v-model:current-page="planPagination.current"
                v-model:page-size="planPagination.size"
                background
                layout="total, prev, pager, next"
                :total="planPagination.total"
                @current-change="handlePlanPageChange"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="同意书与签名" name="consent">
            <div class="split-grid">
              <div>
                <div class="page-header" style="margin-bottom: 12px;">
                  <div>
                    <h3 class="page-title" style="font-size: 18px;">知情同意书</h3>
                  </div>
                  <el-button v-if="hasButtonPermission('EMR_EDIT')" type="primary" link @click="openConsentDialog">新增同意书</el-button>
                </div>
                <el-table :data="consents" size="large">
                  <el-table-column prop="formName" label="名称" min-width="160" />
                  <el-table-column prop="patientName" label="患者" min-width="100" />
                  <el-table-column label="状态" width="120">
                    <template #default="{ row }">
                      <StatusTag :status="row.formStatus" />
                    </template>
                  </el-table-column>
                  <el-table-column fixed="right" label="操作" width="100">
                    <template #default="{ row }">
                      <el-space>
                        <el-button link type="primary" @click="openConsentDetail(row.id)">详情</el-button>
                        <el-button v-if="hasButtonPermission('EMR_EDIT')" link type="success" @click="openConsentDialog(row.id)">编辑</el-button>
                      </el-space>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <div>
                <div class="page-header" style="margin-bottom: 12px;">
                  <div>
                    <h3 class="page-title" style="font-size: 18px;">电子签名</h3>
                  </div>
                  <el-button v-if="hasButtonPermission('EMR_EDIT')" type="primary" link @click="openSignatureDialog">新增签名</el-button>
                </div>
                <el-table :data="signatures" size="large">
                  <el-table-column prop="signerName" label="签署人" min-width="120" />
                  <el-table-column prop="signerType" label="类型" min-width="110" />
                  <el-table-column prop="signedAt" label="签署时间" min-width="150" />
                </el-table>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="模板与牙位图" name="templates">
            <div class="split-grid">
              <div class="page-card" style="box-shadow: none;">
                <div class="el-card__body">
                  <div class="page-header">
                    <div>
                      <h3 class="page-title" style="font-size: 18px;">模板</h3>
                    </div>
                  </div>
                  <el-table :data="templates" size="large" style="margin-top: 12px;">
                    <el-table-column prop="templateName" label="模板名称" min-width="160" />
                    <el-table-column prop="templateType" label="类型" min-width="120" />
                  </el-table>
                </div>
              </div>
              <div class="page-card" style="box-shadow: none;">
                <div class="el-card__body">
                  <div class="page-header">
                    <div>
                      <h3 class="page-title" style="font-size: 18px;">牙位图读取</h3>
                      <p class="page-desc">选择病历后直接读取牙位图详情</p>
                    </div>
                  </div>
                  <div class="toolbar" style="margin-top: 12px;">
                    <EntityRemoteSelect
                      v-model="chartRecordId"
                      :request="fetchRecords"
                      label-key="label"
                      value-key="id"
                      :selected-label="selectedLabels.chartRecord"
                      placeholder="搜索病历"
                    />
                    <el-button @click="loadChart">读取</el-button>
                    <el-button v-if="hasButtonPermission('DENTALCHART_EDIT')" type="primary" plain @click="saveChart">保存牙位图</el-button>
                    <el-button v-if="hasButtonPermission('DENTALCHART_EDIT')" plain @click="addChartDetail">新增牙位</el-button>
                  </div>
                  <el-descriptions v-if="chartData" :column="1" border style="margin-top: 16px;">
                    <el-descriptions-item label="病历号">{{ chartData.medicalRecordNo }}</el-descriptions-item>
                    <el-descriptions-item label="患者">{{ chartData.patientName }}</el-descriptions-item>
                    <el-descriptions-item label="版本">{{ chartData.chartVersion }}</el-descriptions-item>
                    <el-descriptions-item label="状态">{{ chartData.chartStatus }}</el-descriptions-item>
                  </el-descriptions>
                  <el-form :model="chartForm" label-position="top" class="form-grid" style="margin-top: 16px;">
                    <el-form-item label="牙位图类型">
                      <el-select v-model="chartForm.chartType">
                        <el-option label="恒牙" value="ADULT" />
                        <el-option label="乳牙" value="CHILD" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="牙位图状态">
                      <el-select v-model="chartForm.chartStatus">
                        <el-option label="草稿" value="DRAFT" />
                        <el-option label="进行中" value="ACTIVE" />
                        <el-option label="完成" value="COMPLETED" />
                      </el-select>
                    </el-form-item>
                    <el-form-item class="full-span" label="牙位明细">
                      <div class="list" style="width: 100%;">
                        <div v-for="(item, index) in chartForm.details" :key="index" class="list-item" style="align-items: start;">
                          <div style="display: grid; gap: 12px; flex: 1; grid-template-columns: 90px 110px 120px 100px 100px 1fr;">
                            <el-input v-model.trim="item.toothNo" placeholder="牙位" />
                            <el-input v-model.trim="item.toothSurface" placeholder="面位" />
                            <el-select v-model="item.toothStatus">
                              <el-option label="正常" value="NORMAL" />
                              <el-option label="龋坏" value="CARIES" />
                              <el-option label="缺失" value="MISSING" />
                              <el-option label="修复中" value="TREATING" />
                            </el-select>
                            <el-switch v-model="item.diagnosisFlag" :active-value="1" :inactive-value="0" active-text="诊断" />
                            <el-switch v-model="item.treatmentFlag" :active-value="1" :inactive-value="0" active-text="治疗" />
                            <el-input v-model.trim="item.notes" placeholder="备注" />
                          </div>
                          <el-button link type="danger" @click="removeChartDetail(index)">删除</el-button>
                        </div>
                        <div v-if="chartForm.details.length === 0" class="empty-block">
                          <div>当前牙位图还没有牙位明细，点击“新增牙位”开始录入</div>
                        </div>
                      </div>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="recordDialogVisible" :title="currentRecordId ? '编辑病历' : '新建病历'" width="820px">
      <el-form :model="recordForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="recordForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect
            v-model="recordForm.patientId"
            :request="fetchPatients"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.recordPatient"
            placeholder="搜索患者"
          />
        </el-form-item>
        <el-form-item label="预约">
          <EntityRemoteSelect
            v-model="recordForm.appointmentId"
            :request="fetchAppointments"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.appointment"
            placeholder="搜索预约"
          />
        </el-form-item>
        <el-form-item label="医生"><el-select v-model="recordForm.doctorId"><el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" /></el-select></el-form-item>
        <el-form-item label="助理"><el-select v-model="recordForm.assistantId" clearable><el-option v-for="item in assistants" :key="item.id" :label="item.staffName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="就诊类型"><el-select v-model="recordForm.visitType"><el-option label="初诊" value="FIRST" /><el-option label="复诊" value="REVISIT" /></el-select></el-form-item>
        <el-form-item label="就诊时间"><el-date-picker v-model="recordForm.visitDate" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%;" /></el-form-item>
        <el-form-item class="full-span" label="主诉"><el-input v-model="recordForm.chiefComplaint" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="现病史"><el-input v-model="recordForm.presentIllness" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="口腔检查"><el-input v-model="recordForm.oralExamination" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="辅助检查"><el-input v-model="recordForm.auxiliaryExamination" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="初步诊断"><el-input v-model="recordForm.preliminaryDiagnosis" /></el-form-item>
        <el-form-item class="full-span" label="治疗建议"><el-input v-model="recordForm.treatmentAdvice" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="医嘱"><el-input v-model="recordForm.doctorAdvice" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="复诊建议"><el-input v-model="recordForm.revisitAdvice" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="下次复诊"><el-date-picker v-model="recordForm.nextVisitDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item label="病历状态">
          <el-select v-model="recordForm.recordStatus">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="备注"><el-input v-model="recordForm.remark" type="textarea" :rows="2" /></el-form-item>
        <el-form-item class="full-span" label="诊断记录">
          <div class="list" style="width: 100%;">
            <div v-for="(item, index) in recordForm.diagnoses" :key="index" class="list-item" style="align-items: start;">
              <div style="display: grid; gap: 12px; flex: 1; grid-template-columns: 120px 1.4fr 120px 1.4fr;">
                <el-select v-model="item.diagnosisType">
                  <el-option label="初诊" value="INITIAL" />
                  <el-option label="复诊" value="FOLLOW_UP" />
                  <el-option label="终诊" value="FINAL" />
                </el-select>
                <el-input v-model.trim="item.diagnosisName" placeholder="诊断名称" />
                <el-input v-model.trim="item.toothPosition" placeholder="牙位" />
                <el-input v-model.trim="item.diagnosisDesc" placeholder="诊断说明" />
              </div>
              <el-button link type="danger" @click="removeDiagnosis(index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addDiagnosis">新增诊断</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecord">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="planDialogVisible" title="新建治疗计划" width="760px">
      <el-form :model="planForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="planForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect
            v-model="planForm.patientId"
            :request="fetchPatients"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.planPatient"
            placeholder="搜索患者"
          />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect
            v-model="planForm.medicalRecordId"
            :request="fetchRecords"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.planRecord"
            placeholder="搜索病历"
          />
        </el-form-item>
        <el-form-item label="医生"><el-select v-model="planForm.doctorId"><el-option v-for="item in doctors" :key="item.doctorId" :label="item.doctorName" :value="item.doctorId" /></el-select></el-form-item>
        <el-form-item class="full-span" label="计划名称"><el-input v-model.trim="planForm.planName" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="planForm.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="planForm.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" /></el-form-item>
        <el-form-item class="full-span" label="治疗项目">
          <div class="list" style="width: 100%;">
            <div v-for="(item, index) in planForm.items" :key="index" class="list-item" style="align-items: start;">
              <div style="display: grid; gap: 12px; flex: 1; grid-template-columns: 1fr 1.2fr 1fr 120px 100px 120px;">
                <el-input v-model.trim="item.itemCode" placeholder="项目编码" />
                <el-input v-model.trim="item.itemName" placeholder="项目名称" />
                <el-input v-model.trim="item.itemCategory" placeholder="分类" />
                <el-input-number v-model="item.unitPrice" :min="0" style="width: 100%;" />
                <el-input-number v-model="item.quantity" :min="1" style="width: 100%;" />
                <el-input-number v-model="item.discountAmount" :min="0" style="width: 100%;" />
              </div>
              <el-button link type="danger" @click="removePlanItem(index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addPlanItem">新增项目</el-button>
          </div>
        </el-form-item>
        <el-form-item class="full-span" label="备注"><el-input v-model.trim="planForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createPlan">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="consentDialogVisible" :title="currentConsentId ? '编辑知情同意书' : '新增知情同意书'" width="720px">
      <el-form :model="consentForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="consentForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect
            v-model="consentForm.patientId"
            :request="fetchPatients"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.consentPatient"
            placeholder="搜索患者"
          />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect
            v-model="consentForm.medicalRecordId"
            :request="fetchRecords"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.consentRecord"
            placeholder="搜索病历"
          />
        </el-form-item>
        <el-form-item label="模板">
          <el-select v-model="consentForm.templateId" filterable>
            <el-option v-for="item in consentTemplates" :key="item.id" :label="item.templateName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="签署状态">
          <el-select v-model="consentForm.formStatus">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已签署" value="SIGNED" />
            <el-option label="作废" value="VOID" />
          </el-select>
        </el-form-item>
        <el-form-item label="患者/监护人签名">
          <el-select v-model="consentForm.signerSignatureId" clearable filterable>
            <el-option v-for="item in signerSignatureOptions" :key="item.id" :label="`${item.signerName} / ${item.signerType}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生签名">
          <el-select v-model="consentForm.doctorSignatureId" clearable filterable>
            <el-option v-for="item in doctorSignatureOptions" :key="item.id" :label="`${item.signerName} / ${item.medicalRecordNo || '-'}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item class="full-span" label="表单内容">
          <el-input v-model="consentForm.formContent" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item class="full-span" label="模板预览">
          <el-input :model-value="selectedConsentTemplate?.content || '请选择模板查看内容'" type="textarea" :rows="6" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="consentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createConsent">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="signatureDialogVisible" title="新增电子签名" width="680px">
      <el-form :model="signatureForm" label-position="top" class="form-grid">
        <el-form-item label="门诊"><el-select v-model="signatureForm.clinicId"><el-option v-for="item in clinics" :key="item.id" :label="item.clinicName" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="患者">
          <EntityRemoteSelect
            v-model="signatureForm.patientId"
            :request="fetchPatients"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.signaturePatient"
            placeholder="搜索患者"
          />
        </el-form-item>
        <el-form-item label="病历">
          <EntityRemoteSelect
            v-model="signatureForm.medicalRecordId"
            :request="fetchRecords"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.signatureRecord"
            placeholder="搜索病历"
          />
        </el-form-item>
        <el-form-item label="签署人"><el-input v-model.trim="signatureForm.signerName" /></el-form-item>
        <el-form-item label="签署类型"><el-select v-model="signatureForm.signerType"><el-option label="医生" value="DOCTOR" /><el-option label="患者" value="PATIENT" /><el-option label="监护人" value="GUARDIAN" /></el-select></el-form-item>
        <el-form-item label="签名文件">
          <EntityRemoteSelect
            v-model="signatureForm.signatureFileId"
            :request="fetchSignatureFiles"
            label-key="label"
            value-key="id"
            :selected-label="selectedLabels.signatureFile"
            placeholder="搜索签名附件"
          />
        </el-form-item>
        <el-form-item class="full-span" label="上传签名文件">
          <div class="toolbar">
            <input type="file" @change="handleSignatureFileChange" />
            <el-button type="primary" plain @click="uploadSignatureFile">上传并绑定</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="signatureDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createSignature">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="recordDetailVisible" title="病历详情" size="760px">
      <template v-if="recordDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="病历号">{{ recordDetail.recordNo }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ recordDetail.patientName }}</el-descriptions-item>
          <el-descriptions-item label="门诊">{{ recordDetail.clinicName }}</el-descriptions-item>
          <el-descriptions-item label="医生">{{ recordDetail.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="就诊类型">{{ recordDetail.visitType }}</el-descriptions-item>
          <el-descriptions-item label="就诊时间">{{ recordDetail.visitDate }}</el-descriptions-item>
          <el-descriptions-item label="主诉" :span="2">{{ recordDetail.chiefComplaint || '-' }}</el-descriptions-item>
          <el-descriptions-item label="现病史" :span="2">{{ recordDetail.presentIllness || '-' }}</el-descriptions-item>
          <el-descriptions-item label="口腔检查" :span="2">{{ recordDetail.oralExamination || '-' }}</el-descriptions-item>
          <el-descriptions-item label="辅助检查" :span="2">{{ recordDetail.auxiliaryExamination || '-' }}</el-descriptions-item>
          <el-descriptions-item label="初步诊断" :span="2">{{ recordDetail.preliminaryDiagnosis || '-' }}</el-descriptions-item>
          <el-descriptions-item label="治疗建议" :span="2">{{ recordDetail.treatmentAdvice || '-' }}</el-descriptions-item>
          <el-descriptions-item label="医嘱" :span="2">{{ recordDetail.doctorAdvice || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复诊建议" :span="2">{{ recordDetail.revisitAdvice || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下次复诊">{{ recordDetail.nextVisitDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ recordDetail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="page-card" style="box-shadow: none; margin-top: 20px;">
          <div class="el-card__body">
            <div class="page-header">
              <div><h3 class="page-title" style="font-size: 18px;">诊断记录</h3></div>
            </div>
            <el-table :data="recordDetail.diagnoses || []" size="large" style="margin-top: 12px;">
              <el-table-column prop="diagnosisType" label="类型" min-width="100" />
              <el-table-column prop="diagnosisName" label="诊断名称" min-width="180" />
              <el-table-column prop="toothPosition" label="牙位" min-width="100" />
            </el-table>
          </div>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="planDetailVisible" title="治疗计划详情" size="760px">
      <template v-if="planDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划号">{{ planDetail.planNo }}</el-descriptions-item>
          <el-descriptions-item label="计划名称">{{ planDetail.planName }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ planDetail.patientName }}</el-descriptions-item>
          <el-descriptions-item label="医生">{{ planDetail.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="病历号">{{ planDetail.medicalRecordNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ planDetail.planStatus }}</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ planDetail.startDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ planDetail.endDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ planDetail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="page-card" style="box-shadow: none; margin-top: 20px;">
          <div class="el-card__body">
            <div class="page-header">
              <div><h3 class="page-title" style="font-size: 18px;">治疗项目</h3></div>
            </div>
            <el-table :data="planDetail.items || []" size="large" style="margin-top: 12px;">
              <el-table-column prop="itemCode" label="项目编码" min-width="120" />
              <el-table-column prop="itemName" label="项目名称" min-width="180" />
              <el-table-column prop="itemCategory" label="分类" min-width="120" />
              <el-table-column prop="unitPrice" label="单价" min-width="100" />
              <el-table-column prop="quantity" label="数量" min-width="80" />
              <el-table-column prop="receivableAmount" label="金额" min-width="100" />
            </el-table>
          </div>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="consentDetailVisible" title="知情同意书详情" size="760px">
      <template v-if="consentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="名称">{{ consentDetail.formName }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ consentDetail.formStatus }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ consentDetail.patientName }}</el-descriptions-item>
          <el-descriptions-item label="病历号">{{ consentDetail.medicalRecordNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="签署时间" :span="2">{{ consentDetail.signedAt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="内容" :span="2">{{ consentDetail.formContent || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { api } from '@/api/service'
import EntityRemoteSelect from '@/components/EntityRemoteSelect.vue'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { usePermission } from '@/composables/usePermission'

const loading = ref(false)
const { hasButtonPermission } = usePermission()
const activeTab = ref('records')
const clinics = ref<any[]>([])
const doctors = ref<any[]>([])
const assistants = ref<any[]>([])
const templates = ref<any[]>([])
const consentTemplates = ref<any[]>([])
const records = ref<any[]>([])
const plans = ref<any[]>([])
const consents = ref<any[]>([])
const signatures = ref<any[]>([])
const chartRecordId = ref<number | undefined>(undefined)
const chartData = ref<any>(null)
const signatureUploadFile = ref<File | null>(null)
const chartForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  chartType: 'ADULT',
  chartStatus: 'DRAFT',
  details: [] as Array<Record<string, any>>,
})
const selectedLabels = reactive<Record<string, string>>({
  recordPatient: '',
  appointment: '',
  planPatient: '',
  planRecord: '',
  consentPatient: '',
  consentRecord: '',
  signaturePatient: '',
  signatureRecord: '',
  signatureFile: '',
  chartRecord: '',
})

const recordDialogVisible = ref(false)
const planDialogVisible = ref(false)
const consentDialogVisible = ref(false)
const signatureDialogVisible = ref(false)
const currentRecordId = ref<number | null>(null)
const currentConsentId = ref<number | null>(null)
const recordDetailVisible = ref(false)
const planDetailVisible = ref(false)
const consentDetailVisible = ref(false)
const recordDetail = ref<any>(null)
const planDetail = ref<any>(null)
const consentDetail = ref<any>(null)

const recordForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  appointmentId: undefined,
  doctorId: undefined,
  assistantId: undefined,
  visitType: 'FIRST',
  visitDate: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
  chiefComplaint: '',
  presentIllness: '',
  oralExamination: '',
  auxiliaryExamination: '',
  preliminaryDiagnosis: '',
  treatmentAdvice: '',
  doctorAdvice: '',
  revisitAdvice: '',
  nextVisitDate: '',
  remark: '',
  recordStatus: 'DRAFT',
  diagnoses: [{ diagnosisType: 'INITIAL', diagnosisName: '', toothPosition: '', diagnosisDesc: '' }],
})

const planForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  medicalRecordId: undefined,
  doctorId: undefined,
  planName: '',
  planStatus: 'CONFIRMED',
  startDate: '',
  endDate: '',
  remark: '',
  items: [
    {
      itemCode: '',
      itemName: '',
      itemCategory: '',
      unitPrice: 200,
      quantity: 1,
      discountAmount: 0,
    },
  ],
})
const filters = reactive({
  clinicId: undefined as number | undefined,
  doctorId: undefined as number | undefined,
  keyword: '',
})
const recordPagination = reactive({ current: 1, size: 10, total: 0 })
const planPagination = reactive({ current: 1, size: 10, total: 0 })

const consentForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  medicalRecordId: undefined,
  templateId: undefined,
  formContent: '',
  signerSignatureId: undefined,
  doctorSignatureId: undefined,
  formStatus: 'DRAFT',
})

const signatureForm = reactive<Record<string, any>>({
  clinicId: undefined,
  patientId: undefined,
  medicalRecordId: undefined,
  signerName: '',
  signerType: 'DOCTOR',
  signatureFileId: undefined,
})
const selectedConsentTemplate = computed(() => consentTemplates.value.find((item: any) => item.id === consentForm.templateId))
const filteredSignatures = computed(() =>
  (signatures.value || []).filter((item: any) => {
    if (consentForm.patientId && item.patientId !== consentForm.patientId) {
      return false
    }
    if (consentForm.medicalRecordId && item.medicalRecordId !== consentForm.medicalRecordId) {
      return false
    }
    return true
  }),
)
const signerSignatureOptions = computed(() =>
  filteredSignatures.value.filter((item: any) => item.signerType === 'PATIENT' || item.signerType === 'GUARDIAN'),
)
const doctorSignatureOptions = computed(() =>
  filteredSignatures.value.filter((item: any) => item.signerType === 'DOCTOR'),
)

async function loadBaseData() {
  const [clinicsRes, doctorsRes, templatesRes, assistantsRes] = await Promise.all([
    api.org.clinics(),
    api.patient.doctorOptions(),
    api.emr.templates({}),
    api.org.staff({ staffType: 'NURSE' }),
  ])
  clinics.value = clinicsRes.data || []
  doctors.value = doctorsRes.data || []
  templates.value = templatesRes.data || []
  assistants.value = assistantsRes.data || []
  consentTemplates.value = templates.value.filter((item: any) => item.templateType === 'CONSENT_FORM')
  const defaultClinic = clinics.value[0]?.id
  recordForm.clinicId ||= defaultClinic
  planForm.clinicId ||= defaultClinic
  consentForm.clinicId ||= defaultClinic
  signatureForm.clinicId ||= defaultClinic
}

async function loadData() {
  loading.value = true
  try {
    const [recordRes, planRes, consentRes, signatureRes] = await Promise.all([
      api.emr.records({
        clinicId: filters.clinicId,
        doctorId: filters.doctorId,
        keyword: filters.keyword || undefined,
        current: recordPagination.current,
        size: recordPagination.size,
      }),
      api.emr.treatmentPlans({
        clinicId: filters.clinicId,
        doctorId: filters.doctorId,
        keyword: filters.keyword || undefined,
        current: planPagination.current,
        size: planPagination.size,
      }),
      api.emr.consentForms({}),
      api.emr.signatures({}),
    ])
    records.value = recordRes.data.records || []
    plans.value = planRes.data.records || []
    recordPagination.total = recordRes.data.total || 0
    planPagination.total = planRes.data.total || 0
    consents.value = consentRes.data || []
    signatures.value = signatureRes.data || []
  } finally {
    loading.value = false
  }
}

async function openRecordDetail(id: number) {
  const response = await api.emr.recordDetail(id)
  recordDetail.value = response.data
  recordDetailVisible.value = true
}

async function openPlanDetail(id: number) {
  const response = await api.emr.treatmentPlanDetail(id)
  planDetail.value = response.data
  planDetailVisible.value = true
}

async function openConsentDetail(id: number) {
  const response = await api.emr.consentFormDetail(id)
  consentDetail.value = response.data
  consentDetailVisible.value = true
}

async function submitRecord() {
  const validDiagnoses = (recordForm.diagnoses || []).filter((item: any) => item.diagnosisName)
  if (validDiagnoses.length === 0) {
    ElMessage.warning('请至少填写一条诊断记录')
    return
  }
  const payload = {
    ...recordForm,
    diagnoses: validDiagnoses,
  }
  if (currentRecordId.value) {
    await api.emr.updateRecord(currentRecordId.value, payload)
    ElMessage.success('病历更新成功')
  } else {
    await api.emr.createRecord(payload)
    ElMessage.success('病历创建成功')
  }
  recordDialogVisible.value = false
  await loadData()
}

function resetRecordForm() {
  Object.assign(recordForm, {
    clinicId: clinics.value[0]?.id,
    patientId: undefined,
    appointmentId: undefined,
    doctorId: undefined,
    visitType: 'FIRST',
    visitDate: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
    chiefComplaint: '',
    presentIllness: '',
    oralExamination: '',
    auxiliaryExamination: '',
    preliminaryDiagnosis: '',
    treatmentAdvice: '',
    doctorAdvice: '',
    revisitAdvice: '',
    nextVisitDate: '',
    remark: '',
    recordStatus: 'DRAFT',
    diagnoses: [{ diagnosisType: 'INITIAL', diagnosisName: '', toothPosition: '', diagnosisDesc: '' }],
  })
  currentRecordId.value = null
  selectedLabels.recordPatient = ''
  selectedLabels.appointment = ''
}

async function openRecordDialog(recordId?: number) {
  if (!recordId) {
    resetRecordForm()
    recordDialogVisible.value = true
    return
  }
  currentRecordId.value = recordId
  const detailRes = await api.emr.recordDetail(recordId)
  const detail = detailRes.data
  Object.assign(recordForm, {
    clinicId: detail.clinicId,
    patientId: detail.patientId,
    appointmentId: detail.appointmentId,
    doctorId: detail.doctorId,
    assistantId: detail.assistantId,
    visitType: detail.visitType,
    visitDate: detail.visitDate,
    chiefComplaint: detail.chiefComplaint,
    presentIllness: detail.presentIllness,
    oralExamination: detail.oralExamination,
    auxiliaryExamination: detail.auxiliaryExamination,
    preliminaryDiagnosis: detail.preliminaryDiagnosis,
    treatmentAdvice: detail.treatmentAdvice,
    doctorAdvice: detail.doctorAdvice,
    revisitAdvice: detail.revisitAdvice,
    nextVisitDate: detail.nextVisitDate,
    remark: detail.remark,
    recordStatus: detail.recordStatus,
    diagnoses: (detail.diagnoses || []).map((item: any) => ({
      diagnosisType: item.diagnosisType,
      diagnosisName: item.diagnosisName,
      toothPosition: item.toothPosition,
      diagnosisDesc: item.diagnosisDesc,
    })),
  })
  selectedLabels.recordPatient = detail.patientName ? `${detail.patientName} / ${detail.patientMobile || ''}` : ''
  selectedLabels.appointment = detail.appointmentNo ? `${detail.appointmentNo} / ${detail.patientName || ''}` : ''
  recordDialogVisible.value = true
}

async function createPlan() {
  const validItems = planForm.items
    .filter((item: any) => item.itemName || item.itemCode)
    .map((item: any, index: number) => ({
      itemCode: item.itemCode || `TP${String(index + 1).padStart(3, '0')}`,
      itemName: item.itemName,
      itemCategory: item.itemCategory || 'GENERAL',
      unitPrice: item.unitPrice,
      quantity: item.quantity,
      discountAmount: item.discountAmount,
      sortNo: index + 1,
    }))
  if (!validItems.length) {
    ElMessage.warning('请至少填写一个治疗项目')
    return
  }
  if (validItems.some((item: any) => !item.itemName)) {
    ElMessage.warning('治疗项目名称不能为空')
    return
  }
  await api.emr.createTreatmentPlan({
    clinicId: planForm.clinicId,
    patientId: planForm.patientId,
    medicalRecordId: planForm.medicalRecordId,
    doctorId: planForm.doctorId,
    planName: planForm.planName,
    planStatus: planForm.planStatus,
    startDate: planForm.startDate || undefined,
    endDate: planForm.endDate || undefined,
    remark: planForm.remark || undefined,
    items: validItems,
  })
  ElMessage.success('治疗计划创建成功')
  planDialogVisible.value = false
  await loadData()
}

function resetPlanForm() {
  Object.assign(planForm, {
    clinicId: clinics.value[0]?.id,
    patientId: undefined,
    medicalRecordId: undefined,
    doctorId: undefined,
    planName: '',
    planStatus: 'CONFIRMED',
    startDate: '',
    endDate: '',
    remark: '',
    items: [
      {
        itemCode: '',
        itemName: '',
        itemCategory: '',
        unitPrice: 200,
        quantity: 1,
        discountAmount: 0,
      },
    ],
  })
  selectedLabels.planPatient = ''
  selectedLabels.planRecord = ''
}

function openPlanDialog() {
  resetPlanForm()
  planDialogVisible.value = true
}

function addDiagnosis() {
  recordForm.diagnoses.push({
    diagnosisType: 'INITIAL',
    diagnosisName: '',
    toothPosition: '',
    diagnosisDesc: '',
  })
}

function removeDiagnosis(index: number) {
  if (recordForm.diagnoses.length === 1) {
    return
  }
  recordForm.diagnoses.splice(index, 1)
}

async function createConsent() {
  if (consentForm.formStatus === 'SIGNED' && (!consentForm.signerSignatureId || !consentForm.doctorSignatureId)) {
    ElMessage.warning('已签署同意书必须选择患者/监护人签名和医生签名')
    return
  }
  if (currentConsentId.value) {
    await api.emr.updateConsentForm(currentConsentId.value, consentForm)
    ElMessage.success('知情同意书更新成功')
  } else {
    await api.emr.createConsentForm(consentForm)
    ElMessage.success('知情同意书创建成功')
  }
  consentDialogVisible.value = false
  await loadData()
}

function resetConsentForm() {
  Object.assign(consentForm, {
    clinicId: clinics.value[0]?.id,
    patientId: undefined,
    medicalRecordId: undefined,
    templateId: consentTemplates.value[0]?.id,
    formContent: consentTemplates.value[0]?.content || '',
    signerSignatureId: undefined,
    doctorSignatureId: undefined,
    formStatus: 'DRAFT',
  })
  currentConsentId.value = null
  selectedLabels.consentPatient = ''
  selectedLabels.consentRecord = ''
}

async function openConsentDialog(consentId?: number) {
  if (!consentId) {
    resetConsentForm()
    consentDialogVisible.value = true
    return
  }
  currentConsentId.value = consentId
  const response = await api.emr.consentFormDetail(consentId)
  const detail = response.data
  Object.assign(consentForm, {
    clinicId: detail.clinicId,
    patientId: detail.patientId,
    medicalRecordId: detail.medicalRecordId,
    templateId: consentTemplates.value.find((item: any) => item.templateCode === detail.formCode)?.id,
    formContent: detail.formContent,
    signerSignatureId: detail.signerSignatureId,
    doctorSignatureId: detail.doctorSignatureId,
    formStatus: detail.formStatus,
  })
  selectedLabels.consentPatient = detail.patientName || ''
  selectedLabels.consentRecord = detail.medicalRecordNo ? `${detail.medicalRecordNo} / ${detail.patientName || ''}` : ''
  consentDialogVisible.value = true
}

watch(
  () => consentForm.templateId,
  (templateId) => {
    const template = consentTemplates.value.find((item: any) => item.id === templateId)
    if (template) {
      consentForm.formContent = template.content || ''
    }
  },
)

async function createSignature() {
  if (!signatureForm.signatureFileId) {
    ElMessage.warning('请先选择或上传签名文件')
    return
  }
  await api.emr.createSignature(signatureForm)
  ElMessage.success('电子签名创建成功')
  signatureDialogVisible.value = false
  await loadData()
}

function resetSignatureForm() {
  Object.assign(signatureForm, {
    clinicId: clinics.value[0]?.id,
    patientId: undefined,
    medicalRecordId: undefined,
    signerName: '',
    signerType: 'DOCTOR',
    signatureFileId: undefined,
  })
  selectedLabels.signaturePatient = ''
  selectedLabels.signatureRecord = ''
  selectedLabels.signatureFile = ''
  signatureUploadFile.value = null
}

function openSignatureDialog() {
  resetSignatureForm()
  signatureDialogVisible.value = true
}

function handleSignatureFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  signatureUploadFile.value = target.files?.[0] || null
}

async function uploadSignatureFile() {
  if (!signatureUploadFile.value) {
    ElMessage.warning('请选择签名文件')
    return
  }
  if (!signatureForm.patientId || !signatureForm.medicalRecordId) {
    ElMessage.warning('请先选择患者和病历')
    return
  }
  const formData = new FormData()
  formData.append('clinicId', String(signatureForm.clinicId || ''))
  formData.append('bizType', 'SIGNATURE')
  formData.append('bizId', String(signatureForm.medicalRecordId))
  formData.append('patientId', String(signatureForm.patientId))
  formData.append('medicalRecordId', String(signatureForm.medicalRecordId))
  formData.append('uploadSource', 'PC')
  formData.append('file', signatureUploadFile.value)
  const response = await api.file.uploadAttachment(formData)
  signatureForm.signatureFileId = response.data.id
  selectedLabels.signatureFile = `${response.data.fileName} / ${response.data.createdAt || ''}`
  ElMessage.success('签名文件上传成功')
}

async function loadChart() {
  if (!chartRecordId.value) {
    return
  }
  const [chartRes, recordRes] = await Promise.all([
    api.dentalChart.getByRecord(chartRecordId.value),
    api.emr.recordDetail(chartRecordId.value),
  ])
  const chart = chartRes.data
  const record = recordRes.data
  chartData.value = chart
  Object.assign(chartForm, {
    clinicId: chart?.clinicId ?? record?.clinicId,
    patientId: chart?.patientId ?? record?.patientId,
    chartType: chart?.chartType || 'ADULT',
    chartStatus: chart?.chartStatus || 'DRAFT',
    details: (chart?.details || []).map((item: any) => ({
      toothNo: item.toothNo,
      toothSurface: item.toothSurface || '',
      toothStatus: item.toothStatus || 'NORMAL',
      diagnosisFlag: item.diagnosisFlag ?? 0,
      treatmentFlag: item.treatmentFlag ?? 0,
      treatmentItemId: item.treatmentItemId,
      notes: item.notes || '',
    })),
  })
}

async function saveChart() {
  if (!chartRecordId.value) {
    ElMessage.warning('请先选择病历')
    return
  }
  const validDetails = chartForm.details
    .filter((item: any) => item.toothNo || item.notes)
    .map((item: any) => ({
      ...item,
      toothSurface: item.toothSurface || undefined,
      notes: item.notes || undefined,
    }))
  if (validDetails.some((item: any) => !item.toothNo)) {
    ElMessage.warning('牙位编号不能为空')
    return
  }
  await api.dentalChart.saveByRecord(chartRecordId.value, {
    clinicId: chartForm.clinicId,
    patientId: chartForm.patientId,
    chartType: chartForm.chartType,
    chartStatus: chartForm.chartStatus,
    details: validDetails,
  })
  ElMessage.success('牙位图保存成功')
  await loadChart()
}

function addChartDetail() {
  chartForm.details.push({
    toothNo: '',
    toothSurface: '',
    toothStatus: 'NORMAL',
    diagnosisFlag: 0,
    treatmentFlag: 0,
    treatmentItemId: undefined,
    notes: '',
  })
}

function removeChartDetail(index: number) {
  chartForm.details.splice(index, 1)
}

function addPlanItem() {
  planForm.items.push({
    itemCode: '',
    itemName: '',
    itemCategory: '',
    unitPrice: 200,
    quantity: 1,
    discountAmount: 0,
  })
}

function removePlanItem(index: number) {
  if (planForm.items.length === 1) {
    return
  }
  planForm.items.splice(index, 1)
}

function resetFilters() {
  filters.clinicId = undefined
  filters.doctorId = undefined
  filters.keyword = ''
  recordPagination.current = 1
  planPagination.current = 1
  loadData()
}

function handleRecordPageChange(value: number) {
  recordPagination.current = value
  loadData()
}

function handlePlanPageChange(value: number) {
  planPagination.current = value
  loadData()
}

async function updatePlanStatus(id: number, status: string) {
  await api.emr.updateTreatmentPlanStatus(id, { planStatus: status })
  ElMessage.success('治疗计划状态已更新')
  await loadData()
  if (planDetailVisible.value && planDetail.value?.id === id) {
    await openPlanDetail(id)
  }
}

async function fetchPatients(keyword: string) {
  const response = await api.patient.page({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.patientName} / ${item.mobile || '-'}`,
  }))
}

async function fetchAppointments(keyword: string) {
  const response = await api.appointment.page({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.appointmentNo} / ${item.patientName} / ${item.appointmentDate}`,
  }))
}

async function fetchRecords(keyword: string) {
  const response = await api.emr.records({ keyword, current: 1, size: 20 })
  return (response.data.records || []).map((item: any) => ({
    id: item.id,
    label: `${item.recordNo} / ${item.patientName} / ${item.visitDate || ''}`,
  }))
}

async function fetchSignatureFiles(_keyword: string) {
  const response = await api.file.attachments({
    bizType: 'SIGNATURE',
    patientId: signatureForm.patientId,
    medicalRecordId: signatureForm.medicalRecordId,
  })
  return (response.data || []).map((item: any) => ({
    id: item.id,
    label: `${item.fileName} / ${item.createdAt || ''}`,
  }))
}

onMounted(async () => {
  await loadBaseData()
  await loadData()
})
</script>
