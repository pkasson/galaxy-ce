/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.server;

import org.mule.galaxy.web.rpc.HeartbeatService;

public class HeartbeatServiceImpl implements HeartbeatService
{
    public void ping(final String clientId)
    {
        // TODO cleanup after GALAXY-245 is done
        // do nothing for now, but potentially track web clients in the future
        System.out.println("\n\n\n\n>>>");
        System.out.println("Heartbeat from " + clientId);
    }
}