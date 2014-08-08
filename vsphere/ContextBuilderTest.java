/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import com.google.common.collect.ImmutableSet;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions;
import java.util.Properties;

import static org.jclouds.vsphere.config.VSphereConstants.JCLOUDS_VSPHERE_VM_PASSWORD;

import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * Date: 2/23/14
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

//@Test(groups = "unit", testName = "ContextBuilderTest")
public class ContextBuilderTest {
   public static void main(String[] args) throws RunNodesException {
      ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());

      Properties overrides = new Properties();
      overrides.setProperty(JCLOUDS_VSPHERE_VM_PASSWORD, "default");


      ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
              .credentials("root", "vmware")
              .endpoint("https://172.31.11.14/sdk")
              .modules(modules)
              .overrides(overrides)
              .buildView(ComputeServiceContext.class);

      TemplateBuilder b = context.getComputeService().templateBuilder();
      TemplateOptions o = context.getComputeService().templateOptions();
      ((VSphereTemplateOptions) o).postConfiguration(false);
      o.tags(ImmutableSet.of("from UnitTest"))
              .nodeNames(ImmutableSet.of("my-clone1"))
              .runScript("cd /tmp; touch test.txt")
              .networks("Dev Admin Network");
      b.imageId("first-vm12-from-template").locationId("default").minRam(6000).options(o);


      // Set images = context.getComputeService().listNodesByIds(ImmutableSet.of("junit-test-9b7"));
      Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("junit-test", 1, b.build());

      System.out.print("");
   }
}
