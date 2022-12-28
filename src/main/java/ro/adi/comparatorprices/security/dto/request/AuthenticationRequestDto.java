package ro.adi.comparatorprices.security.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AuthenticationRequestDto {
    private String username;
    private String password;
}