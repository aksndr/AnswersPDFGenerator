package ru.terralink;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aksndr on 08.01.2016.
 */
public class AnswersPDFGenerator {

    public AnswersPDFGenerator() {}


    private List<HashMap<String, String>> answers = new ArrayList<>();
    private HashMap<String, String> params = new HashMap<>();

    private static Font chapterFont;
    private static Font paragraphFont;
    private static Font normalFont;


    public Map<String, Object> init(){


        try {
            final BaseFont bf = BaseFont.createFont("c:/Windows/Fonts/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            chapterFont = new Font(bf, 14,Font.BOLD);
            paragraphFont = new Font(bf, 12,Font.BOLD);
            normalFont = new Font(bf, 8,Font.BOLD);
        } catch (DocumentException |IOException e) {
            return failed(e.toString());
        }

        return succeed();
    }

    public Map<String, Object> setParams(HashMap<String, String> params){
        this.params = params;
        return succeed();
    }

    public Map<String, Object> addAnswer(HashMap<String, String> answer){
        answers.add(answer);
        return succeed();
    }

    public Map<String, Object> generatePdf(){
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, bos);
            document.open();
            document.add(createChapter());
            document.add(new Paragraph(" "));
            document.add(createTable());
            //document.newPage();
            document.close();
            bos.close();
        } catch (DocumentException | IOException e) {
            return failed(e.toString());
        }
        return succeed(bos.toByteArray());
    }

    private Element createChapter() throws UnsupportedEncodingException {
        Anchor anchor = new Anchor("Результаты заочного голосования", chapterFont);
        Paragraph title = new Paragraph(anchor);
        title.setAlignment(Element.ALIGN_CENTER);
        Chapter chapter = new Chapter(title, 1);
        chapter.setNumberDepth(0);
        chapter.add(new Paragraph("Предмет закупки: " + params.get("subject"), paragraphFont));
        chapter.add(new Paragraph("Вид мероприятия: " + params.get("event"), paragraphFont));
        chapter.add(new Paragraph("Инициатор: " + params.get("initiator"), paragraphFont));
        chapter.add(new Paragraph("Голосующий: " + params.get("voter"),paragraphFont));

        return chapter;
    }

    private Element createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        PdfPCell cell0 = new PdfPCell(new Paragraph(NUMBER, normalFont));
        cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell0.setColspan(1);
        PdfPCell cell1 = new PdfPCell(new Paragraph(QUESTION, normalFont));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell2 = new PdfPCell(new Paragraph(DECISION, normalFont));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell3 = new PdfPCell(new Paragraph(COMMENT, normalFont));
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        int cnt = 1;
        for (HashMap<String,String> answer : answers) {

            table.addCell(new PdfPCell(new Paragraph(Integer.toString(cnt), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("QUESTION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("DECISION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("DECISIONCOMMENT"), normalFont)));
            cnt++;
        }
        float[] columnWidths = new float[]{5f, 30f, 15f, 30f};
        table.setWidths(columnWidths);
        return table;
    }


    private static Map<String, Object> succeed(){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        return result;
    }

    private static Map<String, Object> succeed(Object value){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        result.put("value", value);
        return result;
    }

    private static Map<String, Object> failed(String errMsg){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", false);
        result.put("errMsg", errMsg);
        return result;
    }

    private static final String COMMENT = "Комментарий";
    private static final String DECISION = "Решение";
    private static final String QUESTION = "Вопрос";
    private static final String NUMBER = "№";
}
