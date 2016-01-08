import com.itextpdf.text.DocumentException;
import ru.terralink.AnswersPDFGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aksndr on 08.01.2016.
 */
public class AnswersPDFGeneratorTest {

    public static void main(String[] args) throws IOException, DocumentException {
        AnswersPDFGenerator dfg = new AnswersPDFGenerator();

        Map<String, Object> result = dfg.init();

        HashMap<String, String> params = new HashMap<>();
        params.put("voter", "ФИО голосующего");
        params.put("initiator", "ФИО инициатора");
        params.put("subject", "Предмет закупки");
        params.put("event", "Вид мероприятия");

        result = dfg.setParams(params);

        HashMap<String, String> answer1 = new HashMap<>();
        answer1.put("QUESTION", "Вопрос1");
        answer1.put("DECISION", "Согласен");
        answer1.put("DECISIONCOMMENT", "Комментарий 1");
        result = dfg.addAnswer(answer1);

        HashMap<String, String> answer2 = new HashMap<>();
        answer2.put("QUESTION", "Вопрос21");
        answer2.put("DECISION", "Не согласен");
        answer2.put("DECISIONCOMMENT", "Комментарий 2");
        result = dfg.addAnswer(answer2);

        HashMap<String, String> answer3 = new HashMap<>();
        answer3.put("QUESTION", "Вопрос3");
        answer3.put("DECISION", "Воздержался");
        answer3.put("DECISIONCOMMENT", "Комментарий 3");
        result = dfg.addAnswer(answer3);

        result = dfg.generatePdf();

        if((Boolean)result.get("ok")){
            byte[] b = (byte[])result.get("value");
            FileOutputStream fos = new FileOutputStream("D:/RND/workspace/AnswersPDFGenerator/tests/2.pdf");
            fos.write(b);
            fos.close();
        }



    }

}
