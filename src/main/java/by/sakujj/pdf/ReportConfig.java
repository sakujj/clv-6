package by.sakujj.pdf;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class ReportConfig {
        private final String boldFontPath;
        private final String plainFontPath;

        private float defaultFontSize = 14;

        private float leftMarginToWidthRatio = 0f;
        private float rightMarginToWidthRatio = 0f;
        private float topMarginToHeightRatio = 0f;
        private float bottomMarginToHeightRatio = 0f;

        private float footerFromBottom = 30;
}
