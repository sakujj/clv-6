package by.sakujj.pdf;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ReportPDF {
    private final String methodName;
    private final List<String> reportInfoList;
    private final ReportPDFConfig config;
}
