package op.warehouse.backend.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import op.warehouse.backend.dto.ResponseCodeEnum;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
    private ResponseCodeEnum responseCode;

    public BaseException(ResponseCodeEnum responseCode, String message) {
        super(message);

        setResponseCode(responseCode);
    }
}
