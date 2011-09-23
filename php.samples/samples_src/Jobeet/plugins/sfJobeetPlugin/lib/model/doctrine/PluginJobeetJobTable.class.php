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

/**
 * This class has been auto-generated by the Doctrine ORM Framework
 */
abstract class PluginJobeetJobTable extends Doctrine_Table
{
  static public $types = array(
    'full-time' => 'Full time',
    'part-time' => 'Part time',
    'freelance' => 'Freelance',
  );

  public function getForLuceneQuery($query)
  {
    $hits = $this->getLuceneIndex()->find($query);
 
    $pks = array();
    foreach ($hits as $hit)
    {
      $pks[] = $hit->pk;
    }
 
    if (empty($pks))
    {
      return array();
    }
 
    $q = $this->createQuery('j')
      ->whereIn('j.id', $pks)
      ->limit(20);
    $q = $this->addActiveJobsQuery($q);
 
    return $q->execute();
  }

  public function getLuceneIndex()
  {
    ProjectConfiguration::registerZend();
 
    if (file_exists($index = $this->getLuceneIndexFile()))
    {
      return Zend_Search_Lucene::open($index);
    }
    else
    {
      return Zend_Search_Lucene::create($index);
    }
  }
 
  public function getLuceneIndexFile()
  {
    return sfConfig::get('sf_data_dir').'/job.'.sfConfig::get('sf_environment').'.index';
  }

  public function getForToken(array $parameters)
  {
    $affiliate = Doctrine::getTable('JobeetAffiliate')->findOneByToken($parameters['token']);
    if (!$affiliate || !$affiliate->getIsActive())
    {
      throw new sfError404Exception(sprintf('Affiliate with token "%s" does not exist or is not activated.', $parameters['token']));
    }

    return $affiliate->getActiveJobs();
  }

  public function getLatestPost()
  {
    $q = Doctrine_Query::create()
      ->from('JobeetJob j');
    $this->addActiveJobsQuery($q);

    return $q->fetchOne();
  }

  public function retrieveBackendJobList(Doctrine_Query $q)
  {
    $rootAlias = $q->getRootAlias();
    $q->leftJoin($rootAlias . '.JobeetCategory c');
    return $q;
  }

  public function cleanup($days)
  {
    $q = $this->createQuery('a')
      ->delete()
      ->andWhere('a.is_activated = ?', 0)
      ->andWhere('a.created_at < ?', date('Y-m-d', time() - 86400 * $days));
 
    return $q->execute();
  }

  public function getTypes()
  {
    return self::$types;
  }

  public function retrieveActiveJob(Doctrine_Query $q)
  {
    return $this->addActiveJobsQuery($q)->fetchOne();
  }
 
  public function getActiveJobs(Doctrine_Query $q = null)
  {
    return $this->addActiveJobsQuery($q)->execute();
  }
 
  public function countActiveJobs(Doctrine_Query $q = null)
  {
    return $this->addActiveJobsQuery($q)->count();
  }
 
  public function addActiveJobsQuery(Doctrine_Query $q = null)
  {
    if (is_null($q))
    {
      $q = Doctrine_Query::create()
        ->from('JobeetJob j');
    }

    $alias = $q->getRootAlias();

    $q->andWhere($alias . '.expires_at > ?', date('Y-m-d h:i:s', time()))
      ->andWhere($alias . '.is_activated = ?', 1)
      ->addOrderBy($alias . '.expires_at DESC');
 
    return $q;
  }
}