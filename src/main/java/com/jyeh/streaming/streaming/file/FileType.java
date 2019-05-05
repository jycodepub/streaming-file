package com.jyeh.streaming.streaming.file;

public enum FileType {
    PDF("pdf"), XML("xml"), DNE("dne"), UNKNOWN("NA");

    private String extention;

    FileType(String extention) {
        this.extention = extention;
    }

    public String getExtention() {
        return extention;
    }

    public static FileType checkFileType(String filename) {
        String normalizedFilename = filename.toLowerCase();
        if (normalizedFilename.contains(PDF.getExtention()))
            return PDF;
        else if (normalizedFilename.contains(XML.getExtention()))
            return XML;
        else if (normalizedFilename.contains(DNE.getExtention()))
            return DNE;
        else
            return UNKNOWN;
    }
}
