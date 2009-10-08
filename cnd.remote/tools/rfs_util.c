/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include "rfs_util.h"

#if TRACE

static const char* pattern = "%u #%s[%d]: ";
static const char* prefix = 0;

static unsigned long get_timestamp() {
    struct timespec tp;
    clock_gettime(CLOCK_REALTIME, &tp);
    return tp.tv_sec*1000000000+tp.tv_nsec;
}

FILE *trace_file;
void trace(const char *format, ...) {
    fprintf(trace_file, pattern, get_timestamp(), prefix, getpid());
    va_list args;
    va_start (args, format);
    vfprintf(trace_file, format, args);
    va_end (args);
    fflush(trace_file);
}

void trace_startup(const char* _prefix, const char* env_var, const char* binary) {
    prefix = _prefix;
    char *file_name = env_var ? getenv(env_var) : NULL;
    binary = binary ? binary : "";
    if (file_name) {
        trace_file = fopen(file_name, "a");
        if (trace_file) {
            fprintf(stderr, "Redirecting trace to %s\n", file_name);
            fprintf(trace_file, "\n\n--------------------\n");
            fflush(trace_file);
        } else {
            fprintf(stderr, "Redirecting trace to %s failed.\n", file_name);
            trace_file = stderr;
        }
    } else {
        trace_file = stderr;
    }
    char dir[PATH_MAX];
    getcwd(dir, sizeof dir);
    trace("%s started in %s\n", binary, dir);
}

void trace_shutdown() {
    if (trace_file && trace_file != stderr) {
        fclose(trace_file);
    }
}

#endif
