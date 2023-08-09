package dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPayload implements Serializable {
    private String key;
    private String paymentType;
    private Date expiredAt;
}
