package task16;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class Task16 {
    @Test
    void task16Test() {

        String key="";
        String token="";
        String boardName="aqa_"+ UUID.randomUUID().toString().substring(0,5);
        String createURL= "https://api.trello.com/1/boards/?name="+boardName+"&key="+key+"&token="+token;
        //C
        //https://api.trello.com/1/boards/?name={{boardName}}&key={{key}}&token={{token}}

        //R
        //https://api.trello.com/1/boards/5c078d8ebed66b5fa238a44d?key=06aacd672653757cb826c81e4605ab02&token=53b31836ef880c26c79d755b48c2298c3fd2c0a94094adc266f5411e97423a1e
        HttpRequest request=HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(createURL))
                .build();

        HttpClient client=HttpClient.newHttpClient();

        try {
            HttpResponse response=client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: "+response);
            System.out.println("Response.headers: "+response.headers());
            System.out.println("Response.body: "+response.body());
            ObjectMapper objectMapper=new ObjectMapper();
            CreateBoardResponseModel trelloBoard=objectMapper
                    .readValue(response.body().toString(), CreateBoardResponseModel.class);
            System.out.println("trelloBoard: "+trelloBoard);
            validateResponse(trelloBoard, boardName);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Step
    private void validateResponse(CreateBoardResponseModel actual, String boardName) {
        CreateBoardResponseModel expect=new CreateBoardResponseModel();
        expect.setName(boardName);
        expect.setDesc("");
        expect.setClosed(false);
        expect.setPinned(false);

        SoftAssert softAssert=new SoftAssert();

        softAssert.assertNotNull(actual.getId());
        softAssert.assertEquals(actual.getName(), expect.getName());
        softAssert.assertEquals(actual.getDesc(), expect.getDesc());
        softAssert.assertEquals(actual.isClosed(), expect.isClosed());
        softAssert.assertEquals(actual.isPinned(), expect.isPinned());

        softAssert.assertAll();

    }
}
