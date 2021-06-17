package com.tutenlabs.timezonerest.controller;

import static com.tutenlabs.timezonerest.constants.Constants.CALCULATE_TIME_URL;
import com.tutenlabs.timezonerest.model.TimeResponse;
import com.tutenlabs.timezonerest.model.OffsetBase;
import com.tutenlabs.timezonerest.services.ZoneComparator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TimeController {

    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);

    private List<String> getTimeZoneList(OffsetBase base) {
        LocalDateTime now = LocalDateTime.now();
        return ZoneId.getAvailableZoneIds().stream().map(ZoneId::of).sorted(new ZoneComparator())
                .map(id -> String.format("%s", getOffset(now, id))).collect(Collectors.toList());
    }

    private String getOffset(LocalDateTime dateTime, ZoneId id) {
        return dateTime.atZone(id).getOffset().getId().replace("Z", "+00:00");
    }

    @ResponseBody
    @PostMapping(value = CALCULATE_TIME_URL)
    public ResponseEntity<Map<String, Object>> calculateTime(@RequestParam("time") String time,
            @RequestParam("timezone") Double timezone) {
        logger.info("Calculating time in UTC");
        logger.info("TIME => " + time);
        logger.info("TIMEZONE => " + timezone);

        String timezoneString = String.valueOf(Math.round(timezone));
        Boolean isAdd = false;

        if (timezone > 0) {
            timezoneString = timezoneString.replace("+", "");
        } else {
            isAdd = true;
            timezoneString = timezoneString.replace("-", "");
        }

        Integer timezoneInt = Integer.parseInt(timezoneString);
        if (timezoneInt >= 10) {
            timezoneString = timezoneString + ":" + "00";
        } else if (timezoneInt >= 0 && timezoneInt < 10) {
            timezoneString = "0" + timezoneString + ":" + "00";
        }

        List<String> timezonesList = getTimeZoneList(OffsetBase.UTC);
        String[] timezonesArray = timezonesList.toArray(new String[0]);
        timezonesArray = Arrays.stream(timezonesArray).distinct().toArray(String[]::new);

        boolean found = Arrays.stream(timezonesArray)
                .anyMatch((isAdd ? "-" + timezoneString : "+" + timezoneString)::equals);

        if (found) {
            logger.info("FOUND TIMEZONE");

            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime hora = LocalTime.parse(time);
            hora = isAdd ? hora.plusHours(timezoneInt) : hora.minusHours(timezoneInt);

            TimeResponse timeResponse = new TimeResponse();
            timeResponse.setTime(hora.format(formateador));
            timeResponse.setTimezone("utc");

            Map<String, Object> response = new HashMap<>();
            response.put("response", timeResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.info("NOT FOUND TIMEZONE");

            TimeResponse timeResponse = new TimeResponse();
            timeResponse.setTime("Error");
            timeResponse.setTimezone(
                    "time zone not found (tz)".replace("tz", (isAdd ? "-" + timezoneString : "+" + timezoneString)));

            Map<String, Object> response = new HashMap<>();
            response.put("response", timeResponse);

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}