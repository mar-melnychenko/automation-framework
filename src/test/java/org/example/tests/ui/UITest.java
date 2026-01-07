package org.example.tests.ui;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.example.ui.pages.CareersQualityAssurancePage;
import org.example.ui.pages.HomePage;
import org.example.ui.pages.LeverApplicationPage;
import org.example.ui.pages.OpenPositionsPage;
import org.example.ui.pages.components.JobCard;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Feature("Careers")
public class UITest extends BaseUiTest {

    @Test(description = "Visit home page, go to QA jobs, filter and validate results, open Lever application form")
    @Story("QA Jobs filtering and validation")
    @Severity(SeverityLevel.CRITICAL)
    public void qaJobsShouldBeFilterableAndRedirectToLever() {
        HomePage home = new HomePage().open();
        Assert.assertTrue(home.isOpenedAndMainBlocksLoaded(), "Home page main blocks should be loaded");

        CareersQualityAssurancePage qaPage = new CareersQualityAssurancePage().open();
        OpenPositionsPage openPositions = qaPage.clickSeeAllQaJobs()
                .waitUntilLoaded()
                .filterByLocation("Istanbul, Turkiye")
                .filterByDepartment("Quality Assurance");

        Assert.assertTrue(openPositions.hasJobs(), "Jobs list should be present after filtering");

        List<JobCard> cards = openPositions.jobCards();
        String pos = "No jobs found";
        for (JobCard card : cards) {
            pos = card.positionText();
            String dept = card.departmentText();
            String loc = card.locationText();

            Assert.assertTrue(pos.contains("Quality Assurance"),
                    "Position should contain 'Quality Assurance' but was: " + pos);

            Assert.assertTrue(dept.contains("Quality Assurance") || dept.isBlank(),
                    "Department should contain 'Quality Assurance' (or be blank) but was: " + dept);

            Assert.assertTrue(loc.contains("Istanbul, Turkiye"),
                    "Location should contain 'Istanbul, Turkiye' but was: " + loc);
        }

        LeverApplicationPage lever = cards.getFirst().clickViewRole();
        Assert.assertTrue(lever.isOpened(pos), "Should redirect to Lever application form page");
    }

}
