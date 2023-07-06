package com.hihonor.datacollector.realtime.utils;

public enum PropertiesEnum {
    ABFIELDSNAME("fieldList"),
    ABFIELDSVAL("actionCode,uid,deviceType,uuid,strategies,eventTime,contents,eventType,groupid"),
    ABJSONFIELDSNAME("jsonLocationList"),
    ABJSONFIELDSVAL("eventDe.events.event,eventDe.events_common.upid,eventDe.events_common.properties._model,eventDe.events_common.uuid,eventDe.events.properties.policy_id,eventDe.events.eventtime,eventDe.events.properties,eventDe.events.properties.ab_event_type,eventDe.events.properties.exp_group_id");

    private String value;

    PropertiesEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.value;
    }
}
