
package org.onlab.onos.cli.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.onos.cli.AbstractShellCommand;
import org.onlab.onos.net.Device;
import org.onlab.onos.net.PortNumber;
import org.onlab.onos.net.device.DeviceService;
import org.onlab.onos.net.flow.CompletedBatchOperation;
import org.onlab.onos.net.flow.DefaultFlowRule;
import org.onlab.onos.net.flow.DefaultTrafficSelector;
import org.onlab.onos.net.flow.DefaultTrafficTreatment;
import org.onlab.onos.net.flow.FlowRuleBatchEntry;
import org.onlab.onos.net.flow.FlowRuleBatchOperation;
import org.onlab.onos.net.flow.FlowRuleService;
import org.onlab.onos.net.flow.TrafficSelector;
import org.onlab.onos.net.flow.TrafficTreatment;
import org.onlab.packet.MacAddress;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Installs many many flows.
 */
@Command(scope = "onos", name = "add-flows",
         description = "Installs a flow rules")
public class AddFlowsCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "flowPerDevice", description = "Number of flows to add per device",
              required = true, multiValued = false)
    String flows = null;

    @Argument(index = 1, name = "numOfRuns", description = "Number of iterations",
              required = true, multiValued = false)
    String numOfRuns = null;

    @Override
    protected void execute() {

        FlowRuleService flowService = get(FlowRuleService.class);
        DeviceService deviceService = get(DeviceService.class);

        int flowsPerDevice = Integer.parseInt(flows);
        int num = Integer.parseInt(numOfRuns);

        ArrayList<Long> results = Lists.newArrayList();
        Iterable<Device> devices = deviceService.getDevices();
        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setOutput(PortNumber.portNumber(1)).build();
        TrafficSelector.Builder sbuilder;
        Set<FlowRuleBatchEntry> rules = Sets.newHashSet();
        Set<FlowRuleBatchEntry> remove = Sets.newHashSet();
        for (Device d : devices) {
            for (int i = 0; i < flowsPerDevice; i++) {
                sbuilder = DefaultTrafficSelector.builder();
                sbuilder.matchEthSrc(MacAddress.valueOf(i))
                        .matchEthDst(MacAddress.valueOf(Integer.MAX_VALUE - i));
                rules.add(new FlowRuleBatchEntry(FlowRuleBatchEntry.FlowRuleOperation.ADD,
                                                    new DefaultFlowRule(d.id(), sbuilder.build(), treatment,
                                                                        100, (long) 0, 10, false)));
                remove.add(new FlowRuleBatchEntry(FlowRuleBatchEntry.FlowRuleOperation.REMOVE,
                                                 new DefaultFlowRule(d.id(), sbuilder.build(), treatment,
                                                                     100, (long) 0, 10, false)));

            }
        }
        boolean isSuccess = true;
        for (int i = 0; i < num; i++) {
            long startTime = System.currentTimeMillis();
            Future<CompletedBatchOperation> op = flowService.applyBatch(
                    new FlowRuleBatchOperation(rules));
            try {
                isSuccess &= op.get().isSuccess();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            long endTime = System.currentTimeMillis();
            results.add(endTime - startTime);
            flowService.applyBatch(
                    new FlowRuleBatchOperation(remove));
        }
        if (outputJson()) {
            print("%s", json(new ObjectMapper(), isSuccess, results));
        } else {
            printTime(isSuccess, results);
        }



    }

    private Object json(ObjectMapper mapper, boolean isSuccess, ArrayList<Long> elapsed) {
        ObjectNode result = mapper.createObjectNode();
        result.put("Success", isSuccess);
        ArrayNode node = result.putArray("elapsed-time");
        for (Long v : elapsed) {
            node.add(v);
        }
        return result;
    }

    private void printTime(boolean isSuccess, ArrayList<Long> elapsed) {
        print("Run is %s.", isSuccess ? "success" : "failure");
        for (int i = 0; i < elapsed.size(); i++) {
            print("  Run %s : %s", i, elapsed.get(i));
        }
    }


}