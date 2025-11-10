package task16;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class CreateBoardResponseModel {
    private String id;
    private String name;
    private String desc;
    private Object descData;
    private boolean closed;
    private String idOrganization;
    private Object idEnterprise;
    private boolean pinned;
    private String url;
    private String shortUrl;
    private Prefs prefs;
    private Map<String, String> labelNames;
    private Map<String, Object> limits;
}
