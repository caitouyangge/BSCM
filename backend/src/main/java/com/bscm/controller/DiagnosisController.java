package com.bscm.controller;

import com.bscm.common.Result;
import com.bscm.logging.BusinessLogger;
import com.bscm.service.DiagnosisService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiagnosisController {

  private final DiagnosisService diagnosisService;
  private final BusinessLogger businessLogger;

  @PostMapping("/submit")
  public Result<String> submitDiagnosis(
      @RequestParam("symptoms") String symptoms,
      @RequestParam(value = "images", required = false) List<MultipartFile> images) {
    try {
      String result = diagnosisService.processDiagnosis(symptoms, images);

      Map<String, Object> extraData = new HashMap<>();
      extraData.put("symptomsLength", symptoms.length());
      extraData.put("imageCount", images != null ? images.size() : 0);
      businessLogger.logBusinessEvent("提交诊断", "症状描述长度: " + symptoms.length() + "字符", extraData);

      return Result.success(result);
    } catch (Exception e) {
      businessLogger.logBusinessError("诊断失败", "症状: " + symptoms, e);
      return Result.error("诊断失败: " + e.getMessage());
    }
  }

  @GetMapping("/history")
  public Result<List<Object>> getHistory() {
    try {
      List<Object> history = diagnosisService.getDiagnosisHistory();
      businessLogger.logBusinessEvent("获取诊断历史", "记录数: " + history.size());
      return Result.success(history);
    } catch (Exception e) {
      businessLogger.logBusinessError("获取历史记录失败", "", e);
      return Result.error("获取历史记录失败: " + e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public Result<Object> getDetail(@PathVariable Long id) {
    try {
      Object detail = diagnosisService.getDiagnosisDetail(id);
      businessLogger.logBusinessEvent("获取诊断详情", "记录ID: " + id);
      return Result.success(detail);
    } catch (Exception e) {
      businessLogger.logBusinessError("获取诊断详情失败", "记录ID: " + id, e);
      return Result.error("获取详情失败: " + e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public Result<Void> deleteRecord(@PathVariable Long id) {
    try {
      diagnosisService.deleteDiagnosis(id);
      businessLogger.logBusinessEvent("删除诊断记录", "记录ID: " + id);
      return Result.success(null);
    } catch (Exception e) {
      businessLogger.logBusinessError("删除诊断记录失败", "记录ID: " + id, e);
      return Result.error("删除失败: " + e.getMessage());
    }
  }
}
