/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.torquebox.rack.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.vfs.VirtualFile;
import org.torquebox.mc.AttachmentUtils;
import org.torquebox.rack.spi.RackApplicationFactory;
import org.torquebox.rack.core.RackApplicationFactoryImpl;
import org.torquebox.rack.metadata.RackApplicationMetaData;


/**
 * <pre>
 * Stage: PRE_DESCRIBE
 *    In: RackApplicationMetaData
 *   Out: RackApplicationMetaData, RackApplicationFactory
 * </pre>
 *
 */
public class RackApplicationFactoryDeployer extends AbstractSimpleVFSRealDeployer<RackApplicationMetaData> {

	public RackApplicationFactoryDeployer() {
		super(RackApplicationMetaData.class);
		addOutput(RackApplicationMetaData.class);
		addOutput(BeanMetaData.class);
		setStage(DeploymentStages.PRE_DESCRIBE);
		setRelativeOrder(500);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RackApplicationMetaData metaData) throws DeploymentException {
		String beanName = AttachmentUtils.beanName(unit, RackApplicationFactory.class);
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(beanName, RackApplicationFactoryImpl.class.getName());
		builder.addPropertyMetaData("rackUpScript", metaData.getRackUpScript());
		VirtualFile rackUpScriptLocation = metaData.getRackUpScriptLocation();
		if (rackUpScriptLocation == null) {
			rackUpScriptLocation = metaData.getRackRoot().getChild("config.ru");
		}
		builder.addPropertyMetaData("rackUpScriptLocation", rackUpScriptLocation);
		AttachmentUtils.attach(unit, builder.getBeanMetaData());
		metaData.setRackApplicationFactoryName(beanName);
	}

}
