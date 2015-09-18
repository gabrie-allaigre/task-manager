package com.synaptix.taskmanager.engine.configuration.result;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.synaptix.component.model.IStackResult;

public class DefaultTaskResultDetailBuilder extends AbstractTaskResultDetailBuilder {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private int maxLength = 4000;

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Max length for String, -1 unlimited
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String buildStack(IStackResult stackResult, int maxResultDepth) {
		StringBuilder sb = new StringBuilder();
		buildStack(stackResult, 0, maxResultDepth, sb);
		return maxLength == -1 ? sb.toString() : StringUtils.left(sb.toString(), maxLength);
	}

	private void buildStack(IStackResult stackResult, int currentResultDepth, final int maxResultDepth, StringBuilder sb) {
		if ((stackResult.getClassName() != null) && (stackResult.getResultText() != null)) { // if resultText is null, we ignore that level
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(stackResult.getClassName());
			if (stackResult.getResultCode() != null) {
				sb.append(", ").append(stackResult.getResultCode());
			}
			if (!stackResult.getResultText().isEmpty()) {
				sb.append(": ").append(stackResult.getResultText());
			}
			if (stackResult.getResultDateTime() != null) {
				sb.append(", ended at ").append(dateFormat.format(stackResult.getResultDateTime()));
			}
		} else {
			currentResultDepth -= 1;
		}
		if (currentResultDepth + 1 < maxResultDepth) {
			if (stackResult.getStackResultList() != null) {
				for (IStackResult child : stackResult.getStackResultList()) {
					buildStack(child, currentResultDepth + 1, maxResultDepth, sb);
				}
			}
		}
	}
}
