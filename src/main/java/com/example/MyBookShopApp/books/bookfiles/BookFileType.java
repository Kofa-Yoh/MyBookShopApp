package com.example.MyBookShopApp.books.bookfiles;

public enum BookFileType {

    TXT(".txt"), PDF(".pdf"), EPUB(".epub"), FB2(".fb2");

    private final String fileExtensionString;

    BookFileType(String fileExtensionString) {
        this.fileExtensionString = fileExtensionString;
    }

    public static String getFileExtensionStringByTypeId(Integer typeId) {
       switch (typeId){
           case 1: return BookFileType.TXT.fileExtensionString;
           case 2: return BookFileType.PDF.fileExtensionString;
           case 3: return BookFileType.EPUB.fileExtensionString;
           case 4: return BookFileType.FB2.fileExtensionString;
           default: return "";
       }
    }
}
