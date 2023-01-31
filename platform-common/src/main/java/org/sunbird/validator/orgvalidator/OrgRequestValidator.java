package org.sunbird.validator.orgvalidator;

import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.request.Request;
import org.sunbird.util.ProjectUtil;

public class OrgRequestValidator extends BaseOrgRequestValidator {

  private static final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();

  public void validateCreateOrgRequest(Request orgRequest) {
    validateParam(
        (String) orgRequest.getRequest().get(JsonKey.ORG_TYPE),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.ORG_TYPE);
    validateParam(
        (String) orgRequest.getRequest().get(JsonKey.ORG_NAME),
        ResponseCode.mandatoryParamsMissing,
        JsonKey.ORG_NAME);
    if (!(orgRequest.getRequest().containsKey(JsonKey.IS_TENANT))
        || (orgRequest.getRequest().containsKey(JsonKey.IS_TENANT)
            && null == orgRequest.getRequest().get(JsonKey.IS_TENANT))) {
      throw new ProjectCommonException(
          ResponseCode.mandatoryParamsMissing,
          MessageFormat.format(
              ResponseCode.mandatoryParamsMissing.getErrorMessage(), JsonKey.IS_TENANT),
          ResponseCode.CLIENT_ERROR.getResponseCode());
    }
    validateTenantOrgChannel(orgRequest);
    validateLicense(orgRequest);
    validateLocationIdOrCode(orgRequest);
  }

  private void validateLicense(Request orgRequest) {
    if (orgRequest.getRequest().containsKey(JsonKey.IS_TENANT)
        && (boolean) orgRequest.getRequest().get(JsonKey.IS_TENANT)
        && orgRequest.getRequest().containsKey(JsonKey.LICENSE)
        && StringUtils.isBlank((String) orgRequest.getRequest().get(JsonKey.LICENSE))) {
      throw new ProjectCommonException(
          ResponseCode.invalidParameterValue,
          MessageFormat.format(
              ResponseCode.invalidParameterValue.getErrorMessage(),
              orgRequest.getRequest().get(JsonKey.LICENSE),
              JsonKey.LICENSE),
          ERROR_CODE);
    }
  }

  public void validateUpdateOrgRequest(Request request) {
    validateOrgReference(request);
    if (request.getRequest().containsKey(JsonKey.ROOT_ORG_ID)
        && StringUtils.isEmpty((String) request.getRequest().get(JsonKey.ROOT_ORG_ID))) {
      throw new ProjectCommonException(
          ResponseCode.invalidParameterValue,
          String.format(ResponseCode.invalidParameterValue.getErrorMessage(), JsonKey.ROOT_ORG_ID),
          ERROR_CODE);
    }
    if (request.getRequest().get(JsonKey.STATUS) != null) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestParameter,
          ProjectUtil.formatMessage(
              ResponseCode.invalidRequestParameter.getErrorMessage(), JsonKey.STATUS),
          ERROR_CODE);
    }

    validateTenantOrgChannel(request);
    validateLocationIdOrCode(request);
  }

  public void validateUpdateOrgStatusRequest(Request request) {
    validateOrgReference(request);

    if (!request.getRequest().containsKey(JsonKey.STATUS)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData,
          ResponseCode.invalidRequestData.getErrorMessage(),
          ERROR_CODE);
    }

    if (!(request.getRequest().get(JsonKey.STATUS) instanceof Integer)) {
      throw new ProjectCommonException(
          ResponseCode.invalidRequestData,
          ResponseCode.invalidRequestData.getErrorMessage(),
          ERROR_CODE);
    }
  }

  private void validateLocationIdOrCode(Request orgRequest) {
    validateListParam(orgRequest.getRequest(), JsonKey.LOCATION_IDS, JsonKey.LOCATION_CODE);
    if (orgRequest.getRequest().get(JsonKey.LOCATION_IDS) != null
        && orgRequest.getRequest().get(JsonKey.LOCATION_CODE) != null) {
      ProjectCommonException.throwClientErrorException(
          ResponseCode.errorAttributeConflict,
          MessageFormat.format(
              ResponseCode.errorAttributeConflict.getErrorMessage(),
              JsonKey.LOCATION_CODE,
              JsonKey.LOCATION_IDS));
    }
  }
}
