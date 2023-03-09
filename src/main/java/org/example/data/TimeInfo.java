package org.example.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeInfo {

    Integer backendWorkTime;
    Integer dbWorkTime;

    @Override
    public String toString(){
        return "backendWorkTime: " + backendWorkTime + "; dbWorkTime: " + dbWorkTime;
    }


}
