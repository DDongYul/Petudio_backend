package kuding.petudio.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.DtoConverter;
import kuding.petudio.domain.type.BundleType;
import kuding.petudio.domain.type.PictureType;
import kuding.petudio.etc.Pair;
import kuding.petudio.etc.callback.CheckedExceptionConverterTemplate;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.ColabServerCallService;
import kuding.petudio.service.PromptService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/four-cuts")
public class FourCutsController {

    private final BundleService bundleService;
    private final ColabServerCallService colabServerCallService;
    private final PromptService promptService;
    private final CheckedExceptionConverterTemplate template = new CheckedExceptionConverterTemplate();

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestPart("beforePictures") List<MultipartFile> beforePictures)  {
        List<ServiceParamPictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile beforePicture : beforePictures) {
            pictureDtos.add(new ServiceParamPictureDto(beforePicture.getOriginalFilename(), template.execute(beforePicture::getBytes) ,PictureType.BEFORE, -1));
        }
        Long bundleId = bundleService.createBundle(BundleType.FOUR_AI_PICTURES);
        colabServerCallService.sendBeforePicturesToAiServer(bundleId);
        bundleService.addPicturesToBundle(bundleId, pictureDtos);
        ServiceReturnBundleDto bundleDto = bundleService.findBundleById(bundleId);
        BundleReturnDto bundle = DtoConverter.serviceReturnBundleToBundleReturn(bundleDto);
        return new BaseDto(bundle);
    }

    @GetMapping("/generate")
    public BaseDto sendGeneratedPicture(@RequestParam("bundleId") Long bundleId) {
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        return new BaseDto(bundle);
    }

    @PostMapping("/generate")
    public BaseDto generateAfterPictures(Long bundleId,
                                         @RequestParam("selectedItems") String selectedItems,
                                         @RequestParam("selectedBackground") String selectedBackground,
                                         @RequestParam("animalType") String animalType) throws JsonProcessingException {
        if (!colabServerCallService.checkTrainingComplete(bundleId)) {
            return new BaseDto("Training is not yet complete");
        }
        log.info("animalType = {}", animalType);
        log.info("selectedItem = {}", selectedItems);
        log.info("selectedBackgrounds = {}", selectedBackground);
        ServiceReturnBundleDto bundle = bundleService.findBundleById(bundleId);
        //prompt는 4개씩만 넘어옴
        List<Pair<Integer, String>> prompts = promptService.makePrompt(selectedItems, selectedBackground, bundle.getRandomName(), animalType);
        List<ServiceParamPictureDto> dtoList = IntStream.range(0, prompts.size()).mapToObj(idx -> {
            String prompt = prompts.get(idx).getSecond();
            log.info("prompt = {}", prompt);
            return colabServerCallService.generateAfterPicture(bundleId, prompt, idx + 1);
        }).collect(Collectors.toList());
        bundleService.addPicturesToBundle(bundleId, dtoList);
        bundle = bundleService.findBundleById(bundleId);
        return new BaseDto(bundle);
    }


}



