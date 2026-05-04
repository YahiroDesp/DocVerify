package kz.docverify.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Heading {
    private String style;
    private String text;
}
