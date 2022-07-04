package org.common.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    ERROR_GETTING_ACCOUNT_INFO("Error getting account information."),
    ERROR_GETTING_TRANSFER_EVENT_INFO("Error getting transaction information."),
    ERROR_RECORDING_TRANSFER_EVENT_INFO("Error recording transaction event information."),
    ERROR_UPDATING_ACCOUNT_INFO("Error updating transaction information."),
    INTERNAL_SERVER_ERROR("Internal server error. Please try again after sometime."),
    INVALID_ACCOUNT_NUMBERS("Invalid account numbers."),
    SOURCE_DEST_CAN_NOT_BE_SAME("Source and destination account numbers can not be same."),

    INVALID_SOURCE_ACCOUNT_NUMBER("Invalid source account number."),
    INVALID_DESTINATION_ACCOUNT_NUMBER("Invalid destination account number."),
    NEGATIVE_OR_ZERO_FUND_TRANSFER_NOT_ALLOWED("Negative or zero fund transfer not allowed"),
    INSUFFICIENT_FUNDS_FOR_TRANSFER("Insufficient funds for transfer.");

    private String errorMessage;
}
