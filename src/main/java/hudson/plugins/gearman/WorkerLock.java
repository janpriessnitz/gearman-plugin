/*
 *
 * Copyright 2013 OpenStack Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package hudson.plugins.gearman;

import jenkins.model.Jenkins;
import hudson.model.Computer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class WorkerLock {
  private MyGearmanWorkerImpl workerHoldingLock = null;
  private static final Logger logger = LoggerFactory
          .getLogger(Constants.PLUGIN_LOGGER_NAME);


  public void lock(MyGearmanWorkerImpl worker)
      throws InterruptedException
  {
    while(true) {
      logger.debug("WorkerLock lock request: " + worker);
      synchronized(this) {
        if(workerHoldingLock == null) {
          workerHoldingLock = worker;
          logger.debug("WorkerLock acquired by: " + worker);
          return;
        }
        else {
          this.wait(1000);
        }
      }
    }
  }

  public void unlock(MyGearmanWorkerImpl worker) {
    synchronized(this) {
      logger.debug("WorkerLock being released by: " + worker);
      if(workerHoldingLock == worker) {
        workerHoldingLock = null;
      }
      notifyAll();
    }
  }
}
