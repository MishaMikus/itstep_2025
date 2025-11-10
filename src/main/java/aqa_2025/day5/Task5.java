package aqa_2025.day5;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class Task5 {

    public static void main(String[] args) throws JAXBException {
        json();
        xml();
    }

    private static void xml() throws JAXBException {
        Car car = (Car) JAXBContext.newInstance(Car.class)
                    .createUnmarshaller()
                    .unmarshal(new File("car.xml"));
        System.out.println(car);

        car.setModel("test_model");
        JAXBContext.newInstance(Car.class)
                .createMarshaller()
                .marshal(car, new File("car_output.xml"));
    }

    private static void json() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Car car = objectMapper.readValue(new File("src/main/resources/json_car.json"), Car.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
