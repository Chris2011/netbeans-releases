 /*
  * Copyright (c) 2007, Sun Microsystems, Inc. All rights reserved.
  *
  * Redistribution and use in source and binary forms, with or without
  * modification, are permitted provided that the following conditions are met:
  * 
  * * Redistributions of source code must retain the above copyright notice,
  *   this list of conditions and the following disclaimer.
  * 
  * * Redistributions in binary form must reproduce the above copyright notice,
  *   this list of conditions and the following disclaimer in the documentation
  *   and/or other materials provided with the distribution.
  *
  * * Neither the name of Sun Microsystems, Inc. nor the names of its contributors
  *   may be used to endorse or promote products derived from this software without
  *   specific prior written permission.
  * 
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
  * THE POSSIBILITY OF SUCH DAMAGE.
  */

package enterprise.annot_ovd_interceptor_appclient;

import java.util.List;

import javax.ejb.EJB;
import enterprise.annot_ovd_interceptor_ejb.*;

public class StatelessSessionAppClient {

    @EJB
    private static StatelessSession sless;

    public static void main(String args[]) {
	try {
            sless.initUpperCase("hello, World!!");
            sless.initLowerCase("Build.xml");
        } catch (Exception  badEx) {
		badEx.printStackTrace();
        }

        List<String> upperList = sless.getInterceptorNamesFor("initUpperCase");
	printList("initUpperCase", upperList);

	try {
	    sless.isOddNumber(7);
        } catch (Exception  badEx) {
		badEx.printStackTrace();
        }

	List<String> isOddNumberList = sless.getInterceptorNamesFor("isOddNumber");
	printList("isOddNumber", isOddNumberList);
    }

    private static void printList(String msg, List<String> list) {
	System.out.print("Interceptors invoked for " + msg + "(): ");
	String delimiter = "";
        for (String str : list) {
            System.out.print(delimiter + str);
	    delimiter = ", ";
        }
	System.out.println("}");
    }
}
