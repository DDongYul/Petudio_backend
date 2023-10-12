package kuding.petudio.controller;

import kuding.petudio.controller.dto.BaseDto;
import kuding.petudio.controller.dto.BundleReturnDto;
import kuding.petudio.controller.dto.PictureReturnDto;
import kuding.petudio.domain.PictureType;
import kuding.petudio.service.AiPictureService;
import kuding.petudio.service.BundleService;
import kuding.petudio.service.dto.ServiceParamPictureDto;
import kuding.petudio.service.dto.ServiceReturnBundleDto;
import kuding.petudio.service.dto.ServiceReturnPictureDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/bundle")
public class BundleController {

    private final BundleService bundleService;
    private final AiPictureService aiPictureService;

    /**
     * Bundle 목록 조회
     * @return 번들 리스트에 대한 JSON 형식 데이터
     */
    @GetMapping
    @ResponseBody
    public BaseDto bundleList(@RequestParam int pageOffset, @RequestParam int pageSize) {
        List<ServiceReturnBundleDto> findRecentBundles = bundleService.findRecentPublicBundles(pageOffset, pageSize);
        List<BundleReturnDto> recentBundles = new ArrayList<>();
        for (ServiceReturnBundleDto findRecentBundle : findRecentBundles) {
            List<ServiceReturnPictureDto> findPictures = findRecentBundle.getPictures();
            List<PictureReturnDto> recentPictures = new ArrayList<>();
            for (ServiceReturnPictureDto findPicture : findPictures) {
                recentPictures.add(new PictureReturnDto(findPicture.getId(), findPicture.getOriginalName(), findPicture.getPictureS3Url(), findPicture.getPictureType()));
            }
            recentBundles.add(new BundleReturnDto(findRecentBundle.getId(), recentPictures, findRecentBundle.getBundleType()));
        }
        BaseDto baseDto = new BaseDto();
        baseDto.setData(recentBundles);
        return baseDto;
    }

    /**
     * AI 생성 전 Before 이미지 업로드
     * 업로드 된 이미지를 AI를 통해 변환 후 DB 저장 -> aiPictureService
     */
    @ResponseBody
    @PostMapping("/upload")
    public BaseDto uploadBeforePicture(@RequestPart("beforePicture") MultipartFile beforePicture){
        ServiceParamPictureDto beforePictureDto = new ServiceParamPictureDto(beforePicture.getOriginalFilename(), beforePicture, PictureType.BEFORE);
        aiPictureService.animalToHuman(beforePictureDto);
        BaseDto baseDto = new BaseDto();
        baseDto.setData(null);
        return baseDto;
    }

    /**
     * @param bundleId
     * 커뮤니티 업로드 버튼을 눌렀을 시 실행. 실제 업로드가 아닌 기존에 DB에 저장해둔 사진을 공개 상태로 전환
     */
    @PostMapping("/new")
    public BaseDto uploadBundle(@RequestParam Long bundleId) {
        bundleService.changeToPublic(bundleId);
        BaseDto baseDto = new BaseDto();
        baseDto.setData(null);
        return baseDto;
    }

    @PostMapping("/like/{bundleId}")
    public BaseDto addLikeCount(@PathVariable Long bundleId) {
        bundleService.addLikeCount(bundleId);
        BaseDto baseDto = new BaseDto();
        baseDto.setData(null);
        return baseDto;
    }

    @GetMapping("/s3url/{bundleId}")
    public BaseDto getBundle(@RequestParam Long bundleId) {
        ServiceReturnBundleDto findBundle = bundleService.findBundleById(bundleId);
        List<ServiceReturnPictureDto> pictures = findBundle.getPictures();
        List<PictureReturnDto> pictureReturnDtos = new ArrayList<>();
        for (ServiceReturnPictureDto picture : pictures) {
            pictureReturnDtos.add(new PictureReturnDto(picture.getId(), picture.getOriginalName(), picture.getPictureS3Url(), picture.getPictureType()));
        }
        BaseDto baseDto = new BaseDto();
        baseDto.setData(new BundleReturnDto(findBundle.getId(), pictureReturnDtos, findBundle.getBundleType()));
        return baseDto;
    }
}

