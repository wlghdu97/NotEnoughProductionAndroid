package com.xhlab.test_shared

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
}