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
        assertEquals(22, ProcessData.processPE.getEdgesCount())
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
        assertEquals(4, processGlass.getEdgesCount())

        val processPE = ProcessData.processPE
        processPE.disconnectRecipe(ProcessData.recipeList[9], ProcessData.recipeList[7], ProcessData.itemList[16], true)
        assertEquals(12, processPE.getRecipeDFSTree().getNodeCount())
        assertEquals(21, processPE.getEdgesCount())
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
            processGlass.getConnectionStatus(processGlass.rootRecipe, processGlass.targetOutput)[0].status
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processGlass.getConnectionStatus(ProcessData.recipeList[3], ProcessData.itemList[5])[0].status
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processGlass.getConnectionStatus(ProcessData.recipeList[5], ProcessData.itemList[5])[0].status
        )
        assertEquals(
            Process.ConnectionStatus.UNCONNECTED,
            processGlass.getConnectionStatus(ProcessData.recipeList[0], ProcessData.itemList[0])[0].status
        )
    }

    @Test
    fun processReversedConnectionStatus() {
        val processPE = ProcessData.processPE
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_CHILD,
            processPE.getConnectionStatus(ProcessData.recipeList[7], ProcessData.itemList[16])[0].status
        )
        assertEquals(
            Process.ConnectionStatus.CONNECTED_TO_PARENT,
            processPE.getConnectionStatus(ProcessData.recipeList[9], ProcessData.itemList[16])[0].status
        )
    }

    @Test
    fun processMarkNotConsumed() {
        val processPE = ProcessData.processPE
        processPE.markNotConsumed(ProcessData.recipeList[7], ProcessData.itemList[17], true)
        assertEquals(
            Process.ConnectionStatus.UNCONNECTED,
            processPE.getConnectionStatus(ProcessData.recipeList[7], ProcessData.itemList[17])[0].status
        )
        processPE.markNotConsumed(ProcessData.recipeList[7], ProcessData.itemList[17])
        assertEquals(
            Process.ConnectionStatus.NOT_CONSUMED,
            processPE.getConnectionStatus(ProcessData.recipeList[7], ProcessData.itemList[17])[0].status
        )
    }

    @Test
    fun processElementMap() {
        val processGlassMap = ProcessData.elementList.subList(0, 10)
            .distinctBy { it.unlocalizedName }
            .map { it.unlocalizedName to it }
            .toMap()
        assertEquals(processGlassMap.keys, ProcessData.processGlass.getElementMap().keys)
        val processPEMap = ProcessData.elementList.subList(10, 36)
            .distinctBy { it.unlocalizedName }
            .map { it.unlocalizedName to it }
            .toMap()
        assertEquals(processPEMap.keys, ProcessData.processPE.getElementMap().keys)
    }

    @Test
    fun processRecipeArray() {
        val processGlassSublist = ProcessData.recipeList.subList(0, 7)
        val processGlassRecipeArray = ProcessData.processGlass.getRecipeArray()
        assertEquals(processGlassSublist.size, processGlassRecipeArray.size)
        assertEquals(processGlassRecipeArray.intersect(processGlassSublist).size, processGlassSublist.size)
        val processPESublist = ProcessData.recipeList.subList(7, 18)
        val processPERecipeArray = ProcessData.processPE.getRecipeArray()
        assertEquals(processPESublist.size, processPERecipeArray.size)
        assertEquals(processPERecipeArray.intersect(processPESublist).size, processPESublist.size)
    }

    @Test
    fun processElementKeyList() {
        val processGlassList = ProcessData.elementList.subList(0, 10).map { it.unlocalizedName }.distinct()
        val processGlassKeyList = ProcessData.processGlass.getElementKeyList()
        assertEquals(processGlassList.size, processGlassKeyList.size)
        assertEquals(processGlassKeyList.intersect(processGlassList).size, processGlassList.size)
        val processPEList = ProcessData.elementList.subList(10, 36).map { it.unlocalizedName }.distinct()
        val processPEKeyList = ProcessData.processPE.getElementKeyList()
        assertEquals(processPEList.size, processPEKeyList.size)
        assertEquals(processPEKeyList.intersect(processPEList).size, processPEList.size)
    }

    @Test
    fun processElementLeafKeyList() {
        val processGlassList = setOf(ProcessData.elementList[0]).map { it.unlocalizedName }.distinct()
        val processGlassLeafKeyList = ProcessData.processGlass.getElementLeafKeyList()
        assertEquals(processGlassList.size, processGlassLeafKeyList.size)
        assertEquals(processGlassLeafKeyList.intersect(processGlassList).size, processGlassList.size)
        val processPEList = setOf(
            ProcessData.elementList[12],
            ProcessData.elementList[14],
            ProcessData.elementList[20],
            ProcessData.elementList[31]
        ).map { it.unlocalizedName }.distinct()
        val processPELeafKeyList = ProcessData.processPE.getElementLeafKeyList()
        assertEquals(processPEList.size, processPELeafKeyList.size)
        assertEquals(processPELeafKeyList.intersect(processPEList).size, processPEList.size)
    }
}