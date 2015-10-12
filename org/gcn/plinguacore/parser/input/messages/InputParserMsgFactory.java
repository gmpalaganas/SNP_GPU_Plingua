/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.gcn.plinguacore.parser.input.messages;


/**
 * This class provides a Factory for input parser messages 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */

public class InputParserMsgFactory {

	private static final int LEXICAL_ERROR_MSG = 1;
	private static final int SYNTACTIC_ERROR_MSG = 2;
	private static final int SEMANTICS_ERROR_MSG = 4;
	private static final int INTERNAL_ERROR_MSG = 5;
	private static final int WARNING_MSG = 6;
	private static final int INFO_MSG = 7;

	private static InputParserMsg createMessage(int msgType, String msg,
			String extendedMsg, MsgInterval interval, int verbosityLevel) {
		InputParserMsg msgObj;
		switch (msgType) {
		case LEXICAL_ERROR_MSG:
			msgObj = new InputParserLexicalErrorMsg(msg);
			break;
		case SYNTACTIC_ERROR_MSG:
			msgObj = new InputParserSyntacticErrorMsg(msg);
			break;
		case SEMANTICS_ERROR_MSG:
			msgObj = new InputParserSemanticsErrorMsg(msg);
			break;
		case INTERNAL_ERROR_MSG:
			msgObj = new InputParserSemanticsErrorMsg(msg);
			break;
		case WARNING_MSG:
			msgObj = new InputParserWarningMsg(msg);
			break;
		case INFO_MSG:
			msgObj = new InputParserInfoMsg(msg, verbosityLevel);
			break;
		default:
			throw new IllegalArgumentException("Invalid message type ("
					+ msgType + ")");
		}

		if (interval != null)
			msgObj = new InputParserIntervalMsg(msgObj, interval);
		if (extendedMsg != null)
			msgObj = new InputParserExtendedMsg(msgObj, extendedMsg);
		return msgObj;
	}

	public static InputParserMsg createInternalErrorMsg(String msg) {
		return createMessage(INTERNAL_ERROR_MSG, msg, null, null, 1);
	}

	public static InputParserMsg createLexicalErrorMessage(String msg) {
		return createMessage(LEXICAL_ERROR_MSG, msg, null, null, 1);
	}

	public static InputParserMsg createSyntacticErrorMessage(String msg) {
		return createMessage(SYNTACTIC_ERROR_MSG, msg, null, null, 1);
	}

	public static InputParserMsg createSemanticsErrorMessage(String msg) {
		return createMessage(SEMANTICS_ERROR_MSG, msg, null, null, 1);
	}

	public static InputParserMsg createWarningMessage(String msg) {
		return createMessage(WARNING_MSG, msg, null, null, 2);
	}

	public static InputParserMsg createInfoMessage(String msg,
			int verbosityLevel) {
		return createMessage(INFO_MSG, msg, null, null, verbosityLevel);
	}

	public static InputParserMsg createInternalErrorMessage(String msg,
			MsgInterval interval) {
		return createMessage(INTERNAL_ERROR_MSG, msg, null, interval, 1);
	}

	public static InputParserMsg createLexicalErrorMessage(String msg,
			MsgInterval interval) {
		return createMessage(LEXICAL_ERROR_MSG, msg, null, interval, 1);
	}

	public static InputParserMsg createSyntacticErrorMessage(String msg,
			MsgInterval interval) {
		return createMessage(SYNTACTIC_ERROR_MSG, msg, null, interval, 1);
	}

	public static InputParserMsg createSemanticsErrorMessage(String msg,
			MsgInterval interval) {
		return createMessage(SEMANTICS_ERROR_MSG, msg, null, interval, 1);
	}

	public static InputParserMsg createWarningMessage(String msg,
			MsgInterval interval) {
		return createMessage(WARNING_MSG, msg, null, interval, 2);
	}

	public static InputParserMsg createInfoMessage(String msg,
			MsgInterval interval, int verbosityLevel) {
		return createMessage(INFO_MSG, msg, null, interval, verbosityLevel);
	}

	public static InputParserMsg createInternalErrorMessage(String msg,
			String extendedMsg, MsgInterval interval) {
		return createMessage(INTERNAL_ERROR_MSG, msg, extendedMsg, interval, 1);
	}

	public static InputParserMsg createLexicalErrorMessage(String msg,
			String extendedMsg, MsgInterval interval) {
		return createMessage(LEXICAL_ERROR_MSG, msg, extendedMsg, interval, 1);
	}

	public static InputParserMsg createSyntacticErrorMessage(String msg,
			String extendedMsg, MsgInterval interval) {
		return createMessage(SYNTACTIC_ERROR_MSG, msg, extendedMsg, interval, 1);
	}

	public static InputParserMsg createSemanticsErrorMessage(String msg,
			String extendedMsg, MsgInterval interval) {
		return createMessage(SEMANTICS_ERROR_MSG, msg, extendedMsg, interval, 1);
	}

	public static InputParserMsg createWarningMessage(String msg,
			String extendedMsg, MsgInterval interval) {
		return createMessage(WARNING_MSG, msg, extendedMsg, interval, 2);
	}

	public static InputParserMsg createInfoMessage(String msg,
			String extendedMsg, MsgInterval interval, int verbosityLevel) {
		return createMessage(INFO_MSG, msg, extendedMsg, interval,
				verbosityLevel);
	}

	public static InputParserMsg createWarningMessage(String msg,
			String extendedMsg) {
		return createMessage(WARNING_MSG, msg, extendedMsg, null, 2);
	}

	public static InputParserMsg createInfoMessage(String msg,
			String extendedMsg, int verbosityLevel) {
		return createMessage(INFO_MSG, msg, extendedMsg, null, verbosityLevel);
	}

}
