package com.xhlab.test_shared

import com.xhlab.nep.model.process.Process
import org.junit.Test
import kotlin.test.assertEquals

class ProcessViewTest {

    @Test
    fun processesNodeCount() {
        assertEquals(7, ProcessViewData.processGlass.getRecipeNodeCount())
        assertEquals(7, ProcessViewData.processGlass.getEdgesCount())

        assertEquals(11, ProcessViewData.processPE.getRecipeNodeCount())
        assertEquals(15, ProcessViewData.processPE.getEdgesCount())
    }

    @Test
    fun processGlassTreeNodeCount() {
        assertEquals(8, ProcessViewData.processGlassTree.getNodeCount())
    }

    @Test
    fun processGlassDFSTree() {
        assertEquals(ProcessViewData.processGlass.getRecipeDFSTree(), ProcessViewData.processGlassTree)
    }

    @Test
    fun processPEDFSTree() {
        assertEquals(ProcessViewData.processPE.getRecipeDFSTree(), ProcessViewData.processPETree)
    }

    @Test
    fun processRecipeDisconnection() {
        val processGlass = ProcessViewData.processGlass
        processGlass.disconnectMachineRecipe(
            ProcessViewData.machineRecipeList[4],
            ProcessViewData.machineRecipeList[5],
            ProcessData.itemList[6]
        )
        assertEquals(5, processGlass.getRecipeDFSTree().getNodeCount())
        assertEquals(6, processGlass.getEdgesCount())
    }

    @Test
    fun processRecipeRemoval() {
        val processGlass = ProcessViewData.processGlass
        processGlass.removeRecipeNode(ProcessViewData.machineRecipeList[4].toView())
        assertEquals(5, processGlass.getRecipeDFSTree().getNodeCount())
        assertEquals(5, processGlass.getEdgesCount())
    }

    @Test
    fun processConnectionStatus() {
        val processGlass = ProcessViewData.processGlass
        assertEquals(
            Process.ConnectionStatus.FINAL_OUTPUT,
            processGlass.getConnectionStatus(processGlass.rootRecipe, processGlass.targetOutput)
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processGlass.getConnectionStatus(ProcessViewData.machineRecipeList[3].toView(), ProcessData.itemList[5])
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processGlass.getConnectionStatus(ProcessViewData.machineRecipeList[5].toView(), ProcessData.itemList[5])
        )
        assertEquals(
            Process.ConnectionStatus.UNCONNECTED,
            processGlass.getConnectionStatus(ProcessViewData.machineRecipeList[0].toView(), ProcessData.itemList[0])
        )
    }

    @Test
    fun processReversedConnectionStatus() {
        val processPE = ProcessViewData.processPE
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processPE.getConnectionStatus(ProcessViewData.machineRecipeList[7].toView(), ProcessData.itemList[16])
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processPE.getConnectionStatus(ProcessViewData.machineRecipeList[9].toView(), ProcessData.itemList[16])
        )
    }
}