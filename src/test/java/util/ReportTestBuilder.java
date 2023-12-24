package util;

import by.sakujj.pdf.ReportConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;

@With
@Getter
@NoArgsConstructor(staticName = "aReport")
@AllArgsConstructor
public class ReportTestBuilder implements TestBuilder<List<List<Map.Entry<String, String>>>> {

    private String title = "TITLE";

    private List<List<Map.Entry<String, String>>> reportTables = List.of(
            List.of(
                    Map.entry("111111111", "valvalvalvalval"),
                    Map.entry("111111112", "значение ззнач"),
                    Map.entry("111111113", "знач111ва333")
            ),
            List.of(
                    Map.entry("222111111", "valva222lvalvalval"),
                    Map.entry("222111112", "2з22начение ззнач"),
                    Map.entry("222111113", "знач222ва333")
            ),
            List.of(
                    Map.entry("333333333", "valvalv333alvalval"),
                    Map.entry("333333334", "значение за333знач"),
                    Map.entry("3333333335", "зна333чва333")
            )
    );

    @Getter
    private ReportConfig config = new ReportConfig(
            "pdf-resources/fonts/Hack-Bold.ttf",
            "pdf-resources/fonts/Hack-Regular.ttf")
            .setBottomMarginToHeightRatio(1 / 8f)
            .setTopMarginToHeightRatio(1 / 6f)
            .setLeftMarginToWidthRatio(1 / 8f)
            .setRightMarginToWidthRatio(1 / 8f);

    @Override
    public List<List<Map.Entry<String, String>>> build() {
        return reportTables;
    }
}
