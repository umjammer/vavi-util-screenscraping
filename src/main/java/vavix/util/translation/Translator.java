/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.translation;

import java.io.IOException;
import java.util.Locale;


/**
 * �o�����|��@�̃C���^�[�t�F�[�X�ł��B
 * 
 * @author <a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version 0.00 030225 nsano initial version <br>
 *          0.01 030226 nsano be interface <br>
 *          0.02 030309 nsano repackage <br>
 */
public interface Translator {

    /** ���[�J���Ȍ���ɖ|�󂵂܂��B */
    String toLocal(String word) throws IOException;

    /** �O���[�o���Ȍ���(�p��)�ɖ|�󂵂܂��B */
    String toGlobal(String word) throws IOException;

    /** ���[�J�����̃��P�[�����擾���܂��B */
    Locale getLocalLocale();

    /** �O���[�o�����̃��P�[�����擾���܂��B */
    Locale getGlobalLocal();
}

/* */
