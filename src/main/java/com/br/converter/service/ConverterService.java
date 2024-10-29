package com.br.converter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import com.br.converter.model.ApiResponse;
import com.br.converter.util.FileExtension;
import com.br.converter.util.FormatUtil;
import com.br.converter.util.MsgSystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConverterService<T> {

    private final Path root = Paths.get("files");
    List<List<String>> structure = new ArrayList<>();
    String fileExtension = "";
    public ApiResponse<String> generateFile(List<List<String>> list, FileExtension fileExtension){
        ApiResponse<String> response = new ApiResponse<>();
        structure = list;
        this.fileExtension = fileExtension.toString().toLowerCase();
        String base64 = "";
        switch (fileExtension){
            case CSV, TXT:
                base64 = formatToCsvAndTXT();
                break;
            case JSON:
                base64 = formatToJson();
                break;
            case XML:
                base64 = formatToXml();
                break;
            case DOC, XLS:
                base64 = formatToXlsAndDoc(fileExtension);
                break;
            case SQL:
                base64 = formatToSql();
                break;
            default:
                break;
        }
        return response.of(HttpStatus.OK, MsgSystem.sucGet("Conversão concluída"), base64);
    }

    private String formatToCsvAndTXT() {

        try{
            BufferedWriter document = new BufferedWriter(new FileWriter("files/document.".concat(this.fileExtension)));
            //HEADER
            structure.get(0).forEach(value -> {
                try {
                    document.write(value.concat(","));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            //ROW
            document.newLine();
            for(int i = 1; i < structure.size(); i++){
                structure.get(i).forEach(value -> {
                    try {
                        document.write(FormatUtil.stringify(value).concat(","));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                document.newLine();
            }

            document.close();
            Path filePath = root.resolve("document.".concat(this.fileExtension));
            return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));
        } catch (IOException e){
            return null;
        }
    }

    private String formatToJson() {

        try {
            BufferedWriter document = new BufferedWriter(new FileWriter("files/document.".concat(this.fileExtension)));
            document.write("{");
            document.newLine();
            document.write(FormatUtil.stringify("data").concat(": ["));
            document.newLine();
            for(int i = 1; i < structure.size(); i++) {
                document.write("{");
                document.newLine();
                for (int j = 0; j < structure.get(i).size(); j++) {
                    document.write(FormatUtil.stringify(structure.get(0).get(j)).concat(": "));
                    document.write(FormatUtil.stringify(structure.get(i).get(j)));
                    if (j == structure.get(i).size() - 1) {
                        document.newLine();
                        document.write("},");
                        document.newLine();
                    } else {
                        document.write(",");
                    }
                    document.newLine();
                }
            }
            document.write("]");
            document.newLine();
            document.write("}");
            document.close();
            Path filePath = root.resolve("document.".concat(this.fileExtension));
            return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));

        } catch (IOException e) {
            return null;
        }
    }

    private String formatToXml(){

        try {
            BufferedWriter document = new BufferedWriter(new FileWriter("files/document.".concat(this.fileExtension)));
            document.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            document.newLine();
            document.write("<tabledata>");
            document.newLine();
            document.write("<fields>");
            document.newLine();
            structure.get(0).forEach(value -> {
                try {
                    document.write("<field/>");
                    document.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            document.write("</fields>");
            document.newLine();
            document.write("<data>");
            document.newLine();
            for (int i = 0; i < structure.size(); i++) {
                try {
                    document.write("<row id=".concat(FormatUtil.stringify(String.valueOf(i +1))).concat(">"));
                    document.newLine();
                    for (int j = 0; j < structure.get(i).size(); j++) {
                        document.write("<column-".concat(String.valueOf(j+1))
                                .concat(">").concat(structure.get(i).get(j))
                                .concat("</column-".concat(String.valueOf(j+1))).concat(">"));
                        document.newLine();
                    }
                    document.write("</row>");
                    document.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            document.write("</data>");
            document.newLine();
            document.write("</tabledata>");

            document.close();
            Path filePath = root.resolve("document.".concat(this.fileExtension));
            return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));

        } catch (IOException e) {
            return null;
        }
    }

    private String formatToXlsAndDoc(FileExtension fileExtension){

        try {
            BufferedWriter document = new BufferedWriter(new FileWriter("files/document.".concat(this.fileExtension)));

            if(FileExtension.XLS.equals(fileExtension)){
                document.write("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
                document.newLine();
                document.write("<meta http-equiv=\"content-type\" content=\"application/vnd.ms-excel; charset=UTF-8\">");
            } else {
                document.write("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
                document.newLine();
                document.write("<meta http-equiv=\"content-type\" content=\"application/vnd.ms-word; charset=UTF-8\">");
            }
            document.newLine();
            document.write("<head></head>");
            document.newLine();
            document.write("<body>");
            document.newLine();
            document.write("<table>");
            document.newLine();
            document.write("<thead>");
            document.newLine();
            document.write("<tr>");
            document.newLine();
            structure.get(0).forEach(value -> {
                try {
                    document.write("<th style=\"border-bottom: 0px none rgb(51, 51, 51);border-top: 0px none rgb(51, 51, 51);border-left: 0px none rgb(51, 51, 51);border-right: 0px none rgb(51, 51, 51);\">".concat(value).concat("</th>"));
                    document.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            document.write("</tr>");
            document.newLine();
            document.write("</thead>");
            document.newLine();
            document.write("<tbody>");
            document.newLine();

            for(int i = 1; i < structure.size(); i++) {
                document.write("<tr>");
                document.newLine();
                for (int j = 0; j < structure.get(i).size(); j++) {
                    try {
                        document.write("<td style=\"border-bottom: 0px none rgb(51, 51, 51);border-top: 0px none rgb(51, 51, 51);border-left: 0px none rgb(51, 51, 51);border-right: 0px none rgb(51, 51, 51);\">"
                                .concat(structure.get(i).get(j)).concat("</td>"));
                        document.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                document.write("</tr>");
                document.newLine();
            }

            document.write("</tbody>");
            document.newLine();
            document.write("</table>");
            document.newLine();
            document.write("</body>");
            document.newLine();
            document.write("</html>");

            document.close();
            Path filePath = root.resolve("document.".concat(this.fileExtension));
            return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));

        } catch (IOException e) {
            return null;
        }
    }

    private String formatToSql(){

        try {
            BufferedWriter document = new BufferedWriter(new FileWriter("files/document.".concat(this.fileExtension)));
            //INSERT
            document.write("INSERT INTO portal_transparencia (");
            for(int i = 0; i < structure.get(0).size(); i++) {
                try {
                    document.write(FormatUtil.singleStringify(structure.get(0).get(i)));
                    if (i == structure.get(0).size() - 1) {
                        document.write(")");
                        document.newLine();
                    } else {
                        document.write(",");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            //VALUES
            document.write("VALUES ");
            for(int i = 1; i < structure.size(); i++) {
                document.write("(");
                for (int j = 0; j < structure.get(i).size(); j++) {
                    try {
                        document.write(FormatUtil.singleStringify(structure.get(i).get(j)));
                        if (j == structure.get(i).size() - 1) {
                            document.write("),");
                            document.newLine();
                        } else {
                            document.write(",");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (i == structure.size() - 1)
                    document.newLine();
            }

            document.close();
            Path filePath = root.resolve("document.".concat(this.fileExtension));
            return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));

        } catch (IOException e) {
            return null;
        }
    }

    public List<List<String>> mapToList(Map<String, String> map, FileExtension fileExtension) {
        List<List<String>> listConverted = new ArrayList<>();
        if(fileExtension.equals(FileExtension.JSON) || fileExtension.equals(FileExtension.SQL)){
            List<String> listTitleJSON = new ArrayList<>();
            List<String> listValuesJSON = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()){
                if(!List.of("undefined", "fileExtension").contains(entry.getKey()))
                    listTitleJSON.add(FormatUtil.removeMultipleWhitespace(entry.getKey()));
            }
            for (Map.Entry<String, String> entry : map.entrySet()){
                if(!List.of("undefined", "fileExtension").contains(entry.getKey()))
                    listValuesJSON.add(FormatUtil.removeMultipleWhitespace(entry.getValue()));
            }
            listConverted.add(listTitleJSON);
            listConverted.add(listValuesJSON);
            return listConverted;
        }

        for (Map.Entry<String, String> entry : map.entrySet()){
            if(!List.of("undefined", "fileExtension").contains(entry.getKey()))
                listConverted.add(List.of(
                        FormatUtil.removeMultipleWhitespace(entry.getKey()),
                        FormatUtil.removeMultipleWhitespace(entry.getValue())
                ));
        }
        return listConverted;
    }
}
