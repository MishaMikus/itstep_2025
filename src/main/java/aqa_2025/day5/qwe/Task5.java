package aqa_2025.day5.qwe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Task5 {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        XmlMapper xmlMapper = new XmlMapper();

        FruitBasket basket = mapper.readValue(new File("input.json"), FruitBasket.class);
        basket.setType("exotic");
        basket.setTasty(false);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.json"), basket);

        FruitBasket basketXml = xmlMapper.readValue(new File("input.xml"), FruitBasket.class);
        basketXml.setType("tropical");
        xmlMapper.writerWithDefaultPrettyPrinter().writeValue(new File("output.xml"), basketXml);

        List<FruitBasket> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FruitBasket fb = new FruitBasket();
            fb.setFruits(Arrays.asList("apple" + i, "banana" + i));
            fb.setTasty(i % 2 == 0);
            fb.setType(i % 3 == 0 ? "citrus" : "berry");
            list.add(fb);
        }

        List<FruitBasket> sorted = list.stream()
                .sorted(Comparator.comparing(FruitBasket::getType).thenComparing(FruitBasket::isTasty))
                .collect(Collectors.toList());

        List<FruitBasket> filtered = list.stream()
                .filter(f -> f.isTasty())
                .filter(f -> f.getType().equals("citrus"))
                .collect(Collectors.toList());

        List<String> mainFields = list.stream()
                .map(FruitBasket::getType)
                .collect(Collectors.toList());

        System.out.println("sorted: " + sorted);
        System.out.println("filtered: " + filtered);
        System.out.println("main fields: " + mainFields);
    }
}