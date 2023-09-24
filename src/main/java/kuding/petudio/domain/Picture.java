package kuding.petudio.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture {

    public Picture(String originalName, String path, PictureType pictureType) {
        this.originalName = originalName;
        this.path = path;
        this.pictureType = pictureType;
    }

    @Id
    @GeneratedValue
    @Column(name = "picture_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    private String originalName;
    private String path;// s3에서의 파일 이름, uuid이용해 생성?

    @Enumerated(EnumType.STRING)
    private PictureType pictureType;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
