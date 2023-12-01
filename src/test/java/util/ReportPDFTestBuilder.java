package util;

import by.sakujj.pdf.ReportPDF;
import by.sakujj.pdf.ReportPDFConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@With
@Getter
@NoArgsConstructor(staticName = "aReportPDF")
@AllArgsConstructor
public class ReportPDFTestBuilder implements TestBuilder<ReportPDF> {
    private String methodName = "METHOD NAME";
    private List<String> reportInfoList = List.of("""
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    SOME INFO 1
                    """,
            """
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    НЕКОТОРАЯ ИНФОРМАЦИЯ 2
                    """,
            """
                    123456
                    7890123456
                    78910234567890123456
                    7890123456
                    78910234567890123456
                    7890123456
                    78910234567890123456
                    7890123456
                    78910234567890123456
                    7890123456
                    78910234567890123456
                    7890123456
                    78910234567890123456789102345678901234567
                    8910234567890123456789102345678901234
                    5678910234567890123456
                    7890123456
                    
                    78910234567890
                    SOMETHING some text @#$#$@78910234567890
                    SOMETHING some text @#$#$@
                    """);

    private ReportPDFConfig reportPDFConfig = new ReportPDFConfig(
            "pdf-resources/fonts/Hack-Bold.ttf",
            "pdf-resources/fonts/Hack-Regular.ttf")
            .setBottomMarginToHeightRatio(1 / 8f)
            .setTopMarginToHeightRatio(1 / 6f)
            .setLeftMarginToWidthRatio(1 / 8f)
            .setRightMarginToWidthRatio(1 / 8f);

    @Override
    public ReportPDF build() {
        return new ReportPDF(
                methodName,
                reportInfoList,
                reportPDFConfig
        );
    }
}
