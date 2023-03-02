package luckysheet.impl;

import java.io.Serializable;

/**
 * @author Mars
 * @date 2020/10/28
 * @description
 */
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = -275582248840137389L;

    /*
     * https://dream-num.github.io/LuckysheetDocs/guide/operate.html#backend-return-
     * format
     * 
     * 0: connection is successful, 1: send to the currently connected user, 2: send
     * information to other users, 3: send selection location information, 4: batch
     * message
     */
    private Integer type;

    private String message;

    public String getMessage() {
        return message;
    }

    public Integer getType() {
        return type;
    }

    private String id;

    public String getId() {
        return id;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    private String data;

    public String getData() {
        return data;
    }

    public ResponseDTO(int type, String id, String username, String data) {
        this.type = type;
        this.id = id;
        this.username = username;
        this.data = data;
    }

    public static ResponseDTO success(String id, String username, String data) {
        return new ResponseDTO(1, id, username, data);
    }

    public static ResponseDTO update(String id, String username, String data) {
        return new ResponseDTO(2, id, username, data);
    }

    public static ResponseDTO mv(String id, String username, String data) {
        return new ResponseDTO(3, id, username, data);
    }

    public static ResponseDTO bulkUpdate(String id, String username, String data) {
        return new ResponseDTO(4, id, username, data);
    }

    public ResponseDTO(String id, String username) {
        this.type = 3;
        this.id = id;
        this.username = username;
        this.message = "用户退出";
    }

    public static ResponseDTO exit(String id, String username) {
        return new ResponseDTO(id, username);
    }

}
