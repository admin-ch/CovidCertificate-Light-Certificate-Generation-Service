package ch.admin.bag.covidcertificate.api.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LightCertificateResponseDto {
    private String payload;
    private byte[] qrCode;
}
