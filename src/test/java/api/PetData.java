package api;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PetData {
    private Long id;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;
    private Category category;

}
