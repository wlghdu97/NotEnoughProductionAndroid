package com.xhlab.test_shared

import com.xhlab.nep.model.process.Process
import org.junit.Test
import kotlin.test.assertEquals

class ProcessTest {

    @Test
    fun processesNodeCount() {
        assertEquals(7, ProcessData.processGlass.getRecipeNodeCount())
        assertEquals(7, ProcessData.processGlass.getEdgesCount())

        assertEquals(11, ProcessData.processPE.getRecipeNodeCount())
        assertEquals(15, ProcessData.processPE.getEdgesCount())
    }

    @Test
    fun processGlassTreeNodeCount() {
        assertEquals(8, ProcessData.processGlassTree.getNodeCount())
    }

    @Test
    fun processGlassDFSTree() {
        assertEquals(ProcessData.processGlass.getRecipeDFSTree(), ProcessData.processGlassTree)
    }

    @Test
    fun processPEDFSTree() {
        assertEquals(ProcessData.processPE.getRecipeDFSTree(), ProcessData.processPETree)
    }

    @Test
    fun processRecipeDisconnection() {
        val processGlass = ProcessData.processGlass
        processGlass.disconnectRecipe(ProcessData.recipeList[4], ProcessData.recipeList[5], ProcessData.itemList[6])
        assertEquals(5, processGlass.getRecipeDFSTree().getNodeCount())
        assertEquals(6, processGlass.getEdgesCount())
    }

    @Test
    fun processRecipeRemoval() {
        val processGlass = ProcessData.processGlass
        processGlass.removeRecipeNode(ProcessData.recipeList[4])
        assertEquals(5, processGlass.getRecipeDFSTree().getNodeCount())
        assertEquals(5, processGlass.getEdgesCount())
    }

    @Test
    fun processConnectionStatus() {
        val processGlass = ProcessData.processGlass
        assertEquals(
            Process.ConnectionStatus.FINAL_OUTPUT,
            processGlass.getConnectionStatus(processGlass.rootRecipe, processGlass.targetOutput)
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processGlass.getConnectionStatus(ProcessData.recipeList[3], ProcessData.itemList[5])
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processGlass.getConnectionStatus(ProcessData.recipeList[5], ProcessData.itemList[5])
        )
        assertEquals(
            Process.ConnectionStatus.UNCONNECTED,
            processGlass.getConnectionStatus(ProcessData.recipeList[0], ProcessData.itemList[0])
        )
    }

    @Test
    fun processReversedConnectionStatus() {
        val processPE = ProcessData.processPE
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processPE.getConnectionStatus(ProcessData.recipeList[7], ProcessData.itemList[16])
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processPE.getConnectionStatus(ProcessData.recipeList[9], ProcessData.itemList[16])
        )
    }
}