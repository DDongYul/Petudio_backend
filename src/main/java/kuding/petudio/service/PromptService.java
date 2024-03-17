package kuding.petudio.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuding.petudio.etc.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PromptService {
    Map<String, String> promptConverter = new ConcurrentHashMap<>();
    List<Pair<String, String>> randomPrompt = new ArrayList<>();

    public List<Pair<Integer, String>> makePrompt(String selectedItems, String selectedBackground, String randomName, String animalType) throws JsonProcessingException {
        animalType = animalType.toUpperCase(Locale.ROOT);

        //KU1에 대한 놈만 예외처리
        if (randomName.equals("KU1")) {
            randomName = "KU";
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> objectSelectedBackground = objectMapper.readValue(selectedBackground, new TypeReference<Map<String, String>>() {
        });
        Map<String, List<String>> objectSelectedItems = objectMapper.readValue(selectedItems, new TypeReference<Map<String, List<String>>>() {
        });

        List<Pair<Integer, String>> promptList = new ArrayList<>();

        for (int idx = 1; idx < 5; idx++) {
            String koreaBackground = objectSelectedBackground.get("구역 " + String.valueOf(idx));
            List<String> koreItems = objectSelectedItems.get("구역 " + String.valueOf(idx));
            if(koreaBackground.equals("랜덤")){
                int i = (int)(Math.random() * randomPrompt.size());
                StringBuilder sb = new StringBuilder();
                sb.append(randomPrompt.get(i).getFirst()).append(randomName).append(" ").append(animalType).append(randomPrompt.get(i).getSecond());
                String prompt = sb.toString();
                promptList.add(new Pair<>(idx, prompt));
            }else{
                StringBuilder sb = new StringBuilder();
                sb.append("photo of a ").append(randomName).append(" ").append(animalType);

                if (koreItems.size() != 0) {
                    sb.append(" with ");
                    koreItems.forEach(koreaItem -> {
                        String item = promptConverter.get(koreaItem);
                        sb.append(item).append(",");
                    });
                }
                if(promptConverter.get(koreaBackground) != null){
                    sb.append(" on the background of the ").append(promptConverter.get(koreaBackground));
                }else{
                    sb.append(" elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography,realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced ");
                }

                String prompt = sb.toString();
                promptList.add(new Pair<>(idx, prompt));
            }
        }
        return promptList;
    }

    @PostConstruct
    public void promptNameConverterInit() {
        promptConverter.put("모자", "hat");
        promptConverter.put("선글라스", "sun glass");
        promptConverter.put("수영", "swimming pool");
        promptConverter.put("벚꽃", "sakura, sakura petals");
        promptConverter.put("꽃밭", "flower garden");
        promptConverter.put("피라미드", "pyramid");
        promptConverter.put("경기장", "crowded stadium");
        promptConverter.put("해변", "beach");
    }

    @PostConstruct
    public void randomPromptInit() {
        randomPrompt.add(new Pair<>("photo of a ", " fireman, extinguishes fire, sausages, sausage, hyper-detailed, hyper-realism, sharp frame, cinematic, (background action-packed), lit"));
//        randomPrompt.add(new Pair<>("abstract expressionist painting of an award-winning photo of a ", " hyperrealistic <lora:xl_more_art-full_v1:0.7> <lora:zhibi-sdxl:1> zhibi, energetic brushwork, bold colors, abstract forms, expressive, emotional"));
        randomPrompt.add(new Pair<>("Astral Aura, an award-winning photo of a ", ", hyperrealistic <lora:xl_more_art-full_v1:0.5>, astral, colorful aura, vibrant energy"));
        randomPrompt.add(new Pair<>("photo of a ", " athletes, sausage race, olympic games, hyper-detailed, hyper-realism, sharp frame, cinematic, (background action-packed), lit <lora:lit:1>"));
        randomPrompt.add(new Pair<>("photo of a ", " in Egyptian robes, pharaoh, on the background of the pyramids), hyper-detailed, hyper-realism, sharp shot, cinematic, background action-packed, <lora:xl_more_art-full_v1:1>, <lora:lit:0.5>, effect bokeh"));
        randomPrompt.add(new Pair<>("photo of a ", " in a kimono with a katana in his hands, against the background of sakura, sakura petals), hyper-detailed, hyper-realism, sharp shot, cinematic, background action-packed, <lora:xl_more_art-full_v1:1>, <lora:lit:0.5>, effect bokeh"));
        randomPrompt.add(new Pair<>("photo of a ", ",with hat,with sunglass, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " holding strawberry, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, xf iq 4, 1 5 0 mp, 5 0 mm, iso 2 0 0, 1 / 1 6 0 s, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " eating a hat, elegant, luxury, clean, smooth, elegant, beautiful, highly detailed, sharp focus, studio photography, xf iq 4, 1 5 0 mp, 5 0 mm, iso 2 0 0, 1 / 1 6 0 s, realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", " wearing a karate gi, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  wearing a karate gi, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  perched gracefully on top of a stack of old books, with a dimly lit vintage study room in the background, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "   lazily stretching on a sunny windowsill, with houseplants around and a view of a serene garden outside, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));
        randomPrompt.add(new Pair<>("photo of a ", "  leaping through the air, trying to catch a fluttering butterfly in a sunny flower garden, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced"));

        createRandomPrompt("photo of a ", " hiding in the leaves,(bright screen:1.1),pastel_colors,soft light,natsuki yuu \\(amemizu\\),eyes_focus,depth of field,smile,kind_smile,, (((masterpiece))),(((best quality))),((ultra-detailed))");
        createRandomPrompt("photo of a ", " in a superhero cape, pretending to fly with a backdrop of a city skyline, complete with tall buildings and a setting sun, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " dressed as a chef, standing in a toy kitchen, with miniature cooking pots and a pretend cake, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " leaping to catch a brightly colored ball in mid-air at a sunny park, with a background of blooming flowers and tall trees, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " excitedly digging in the sand at the beach, with ocean waves in the background and a colorful sunset, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " in a playful pose, chasing its tail on a grassy lawn, with a background of a cozy home and a flower garden, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " jumping over a series of small hurdles in an agility course, with an audience of colorful flags and cheering people in the background, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " bounding through deep snow in a winter landscape, with snow-covered trees and a distant mountain range, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " chasing a shadow on a sunny day in a bustling city park, with skyscrapers and a fountain in the background, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
        createRandomPrompt("photo of a ", " spinning and catching a frisbee on a beach at sunset, with the ocean's waves and a vibrant sky as the backdrop, elegant, luxury, clean, smooth,  beautiful, highly detailed, sharp focus, studio photography,  realistic, natural light, octane render, adobe lightroom, rule of thirds, symmetrical balance, depth layering, polarizing filter, sense of depth, ai enhanced");
    }

    private void createRandomPrompt(String first, String second) {
        randomPrompt.add(new Pair<>(first, second));
    }
}
