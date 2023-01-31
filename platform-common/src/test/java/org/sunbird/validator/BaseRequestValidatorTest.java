package org.sunbird.validator;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;
import java.util.*;
import org.junit.Test;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.request.Request;

/** Created by rajatgupta on 20/03/19. */
public class BaseRequestValidatorTest {
  private static final BaseRequestValidator baseRequestValidator = new BaseRequestValidator();

  @Test
  public void testValidateSearchRequestFailureWithInvalidFieldType() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.FILTERS, new HashMap<>());
    requestObj.put(JsonKey.FIELDS, "invalid");
    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getErrorCode());
      assertEquals(
          MessageFormat.format(
              ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List"),
          e.getMessage());
    }
  }

  @Test
  public void testCheckMandatoryFieldsPresent() {
    Map<String, Object> request = new HashMap<>();
    try {
      baseRequestValidator.checkMandatoryFieldsPresent(request, "key");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getErrorCode());
    }
  }

  @Test
  public void testCheckReadOnlyAttributesAbsent() {
    Map<String, Object> request = new HashMap<>();
    try {
      baseRequestValidator.checkReadOnlyAttributesAbsent(request, "key");
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getErrorCode());
    }
  }

  @Test
  public void testCheckMandatoryFieldsPresent2() {
    Map<String, Object> request = new HashMap<>();
    try {
      baseRequestValidator.checkMandatoryFieldsPresent(request, new ArrayList<>());
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.invalidRequestData.getErrorCode(), e.getErrorCode());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFieldsValueInList() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    requestObj.put(JsonKey.FILTERS, new HashMap<>());
    requestObj.put(JsonKey.FIELDS, Arrays.asList(1));
    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(ResponseCode.dataTypeError.getErrorCode(), e.getErrorCode());
      assertEquals(
          MessageFormat.format(
              ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List of String"),
          e.getMessage());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFiltersKeyAsNull() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    Map<String, Object> filterMap = new HashMap<>();
    filterMap.put(null, "data");
    requestObj.put(JsonKey.FILTERS, filterMap);

    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(
          MessageFormat.format(
              ResponseCode.invalidParameterValue.getErrorMessage(), null, JsonKey.FILTERS),
          e.getMessage());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFiltersNullValueInList() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    Map<String, Object> filterMap = new HashMap<>();
    List<String> data = new ArrayList<>();
    data.add(null);
    filterMap.put(JsonKey.FIRST_NAME, data);
    requestObj.put(JsonKey.FILTERS, filterMap);

    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(
          MessageFormat.format(
              ResponseCode.invalidParameterValue.getErrorMessage(), null, JsonKey.FIRST_NAME),
          e.getMessage());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFiltersNullValueInMap() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    Map<String, Object> filterMap = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put(JsonKey.FIRST_NAME, null);
    filterMap.put(JsonKey.FIELD, data);
    requestObj.put(JsonKey.FILTERS, filterMap);

    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(
          MessageFormat.format(
              ResponseCode.invalidParameterValue.getErrorMessage(), null, JsonKey.FIRST_NAME),
          e.getMessage());
    }
  }

  @Test
  public void testValidateSearchRequestFailureWithInvalidFiltersNullValueInString() {
    Request request = new Request();
    Map<String, Object> requestObj = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put(JsonKey.FIRST_NAME, null);

    requestObj.put(JsonKey.FILTERS, data);

    request.setRequest(requestObj);
    try {
      baseRequestValidator.validateSearchRequest(request);
    } catch (ProjectCommonException e) {
      assertEquals(
          MessageFormat.format(
              ResponseCode.invalidParameterValue.getErrorMessage(), null, JsonKey.FIRST_NAME),
          e.getMessage());
    }
  }
}
