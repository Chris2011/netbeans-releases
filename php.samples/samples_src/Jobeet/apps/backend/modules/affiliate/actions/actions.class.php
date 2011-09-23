<?php
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

require_once dirname(__FILE__) . '/../lib/affiliateGeneratorConfiguration.class.php';
require_once dirname(__FILE__) . '/../lib/affiliateGeneratorHelper.class.php';

/**
 * affiliate actions.
 *
 * @package    jobeet
 * @subpackage affiliate
 * @author     Your name here
 * @version    SVN: $Id: actions.class.php 12474 2008-10-31 10:41:27Z fabien $
 */
class affiliateActions extends autoAffiliateActions {

    public function executeListActivate() {
        $affiliate = $this->getRoute()->getObject();
        $affiliate->activate();

        // send an email to the affiliate
        ProjectConfiguration::registerZend();
        $mail = new Zend_Mail();
        $mail->setBodyText(<<<EOF
Your Jobeet affiliate account has been activated.

Your token is {$affiliate->getToken()}.

The Jobeet Bot.
EOF
        );
        $mail->setFrom('jonwage@gmail.com', 'Jobeet Bot');
        $mail->addTo($affiliate->getEmail());
        $mail->setSubject('Jobeet affiliate token');
        $mail->send();

        $this->redirect('@jobeet_affiliate');
    }

    public function executeListDeactivate() {
        $this->getRoute()->getObject()->deactivate();

        $this->redirect('@jobeet_affiliate');
    }

    public function executeBatchActivate(sfWebRequest $request) {
        $q = Doctrine_Query::create()
                ->from('JobeetAffiliate a')
                ->whereIn('a.id', $request->getParameter('ids'));

        $affiliates = $q->execute();

        foreach ($affiliates as $affiliate) {
            $affiliate->activate();
        }

        $this->redirect('@jobeet_affiliate');
    }

    public function executeBatchDeactivate(sfWebRequest $request) {
        $q = Doctrine_Query::create()
                ->from('JobeetAffiliate a')
                ->whereIn('a.id', $request->getParameter('ids'));

        $affiliates = $q->execute();

        foreach ($affiliates as $affiliate) {
            $affiliate->deactivate();
        }

        $this->redirect('@jobeet_affiliate');
    }

}
