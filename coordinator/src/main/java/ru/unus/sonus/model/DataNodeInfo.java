package ru.unus.sonus.model;

import com.google.protobuf.Timestamp;

public record DataNodeInfo(Integer workload, Timestamp timestamp) { }
