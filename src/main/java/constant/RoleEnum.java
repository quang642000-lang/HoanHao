package constant;

public enum RoleEnum {
    ADMIN(1, "Admin"),
    NHAN_VIEN(2, "Nhân Viên");

    private final int code;
    private final String description;

    RoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }
}