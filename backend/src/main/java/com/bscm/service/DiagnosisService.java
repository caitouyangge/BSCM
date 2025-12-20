package com.bscm.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DiagnosisService {
  String processDiagnosis(String symptoms, List<MultipartFile> images);

  List<Object> getDiagnosisHistory();

  Object getDiagnosisDetail(Long id);

  void deleteDiagnosis(Long id);
}
