package com.tutenlabs.timezonerest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeResponse {
    
    private String time;
    
    private String timezone;
}
