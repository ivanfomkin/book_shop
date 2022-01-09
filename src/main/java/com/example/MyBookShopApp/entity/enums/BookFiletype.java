package com.example.MyBookShopApp.entity.enums;

public enum BookFiletype {
    PDF(".pdf"),
    EPUB(".epub"),
    FB2(".fb2");
    private final String fileExtension;

    BookFiletype(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtensionById(int id) {
        return switch (id) {
            case 1 -> PDF.fileExtension;
            case 2 -> EPUB.fileExtension;
            case 3 -> FB2.fileExtension;
            default -> "";
        };
    }
}
