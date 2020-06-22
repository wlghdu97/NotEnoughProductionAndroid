package com.xhlab.test_shared

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
}