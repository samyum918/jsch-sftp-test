import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SftpConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String privateKey;
    private String passphrase;
    private Integer sessionConnectTimeout;
    private Integer channelConnectedTimeout;
}
